package ws.leap.kert.http

import io.vertx.core.Context
import io.vertx.core.MultiMap
import io.vertx.core.buffer.Buffer
import kotlinx.coroutines.flow.Flow
import io.vertx.core.http.HttpClientResponse as VHttpClientResponse

class HttpClientResponse (private val underlying: VHttpClientResponse, private val context: Context) : HttpResponse {
  override val headers: MultiMap = underlying.headers()
  override val trailers: () -> MultiMap = {
    underlying.trailers()
  }

  override val body: Flow<Buffer> = underlying.asFlow(context)
  override val statusCode: Int = underlying.statusCode()
}
