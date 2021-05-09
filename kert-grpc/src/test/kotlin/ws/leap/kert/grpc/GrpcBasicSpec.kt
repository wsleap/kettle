package ws.leap.kert.grpc

import io.kotest.core.spec.DoNotParallelize
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import ws.leap.kert.test.EchoCountReq
import ws.leap.kert.test.EchoGrpcKt
import ws.leap.kert.test.EchoReq

private val logger = KotlinLogging.logger {}

@DoNotParallelize
class GrpcBasicSpec : GrpcSpec() {
  override fun configureServer(builder: GrpcServerBuilder) {
    builder.service(EchoServiceImpl())
  }

  private val stub = EchoGrpcKt.stub(client)

  init {
    context("Grpc server/client") {
      test("unary") {
        val req = EchoReq.newBuilder().setId(1).setValue(EchoTest.message).build()
        val resp = stub.unary(req)
        resp.id shouldBe 1
        resp.value shouldBe EchoTest.message
      }

      test("server stream") {
        val req = EchoCountReq.newBuilder().setCount(EchoTest.streamSize).build()
        val resp = stub.serverStreaming(req)
        val respMsgs = resp.map { msg ->
          logger.trace { "Client received id=${msg.id}" }
          msg
        }.toList()
        respMsgs shouldHaveSize EchoTest.streamSize
      }

      test("client stream") {
        val req = flow {
          for(i in 0 until EchoTest.streamSize) {
            val msg = EchoReq.newBuilder().setId(i).setValue(EchoTest.message).build()
            emit(msg)
            logger.trace { "Client sent id=${msg.id}" }
            delay(1)
          }
        }

        val resp = stub.clientStreaming(req)
        resp.count shouldBe EchoTest.streamSize
      }

      test("bidi stream") {
        val req = flow {
          for(i in 0 until EchoTest.streamSize) {
            val msg = EchoReq.newBuilder().setId(i).setValue(EchoTest.message).build()
            emit(msg)
            logger.trace { "Client sent id=${msg.id}" }
            delay(1)
          }
        }

        val resp = stub.bidiStreaming(req)
        var count = 0
        resp.collect { msg ->
          count++
          logger.trace { "Client received id=${msg.id}" }
        }
        count shouldBe EchoTest.streamSize
      }
    }
  }
}
