package ws.leap.kettle.grpc

import io.grpc.MethodDescriptor
import io.grpc.Status
import ws.leap.kettle.http.HttpClient
import io.vertx.core.buffer.Buffer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.net.URL

// placeholder, nothing to configure right now
class CallOptions {

}

abstract class AbstractStub<S>(
  private val client: HttpClient,
  address: URL,
  protected val callOptions: CallOptions = CallOptions()
) {
  private val scheme = address.protocol
  private val host= address.host
  private val port = if (address.port != -1) address.port else address.defaultPort

  protected fun <ReqT, RespT> newCall(method: MethodDescriptor<ReqT, RespT>,
                                      callOptions: CallOptions
  ): ClientCallHandler<ReqT, RespT> {
    return object : ClientCallHandler<ReqT, RespT> {
      override suspend fun invoke(requests: Flow<ReqT>): Flow<RespT> {
        val requestSerializer = GrpcUtils.requestSerializer(method)
        val responseDeserializer = GrpcUtils.responseDeserializer(method)

        val httpRequestFlow = requests.map { msg ->
          val buf = GrpcUtils.serializeMessagePacket(msg, requestSerializer)
          Buffer.buffer(buf)
        }

        val httpResponse = client.post("/${method.fullMethodName}", httpRequestFlow)
        val responses = GrpcUtils.readMessages(httpResponse.body, responseDeserializer)
        return flow {
          responses.collect { msg ->
            emit(msg)
          }

          // fail the flow if trailer is missing or not OK
          val statusCode = httpResponse.trailers[Constants.status]?.toInt() ?: throw IllegalStateException("GRPC status is missing")
          val status = Status.fromCodeValue(statusCode)
          if (!status.isOk) throw status.asException()
        }
      }
    }
  }

  protected abstract fun build(client: HttpClient, address: URL, callOptions: CallOptions): S
}
