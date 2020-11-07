package ws.leap.kettle.rest

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.core.http.HttpMethod
import ws.leap.kettle.http.HttpRouter
import ws.leap.kettle.http.RouterConfigurator

fun RouterConfigurator.rest(mapper: ObjectMapper, configure: RestRouter.() -> Unit) {
  val router = RestRouter(mapper, http())
  configure(router)
}

class RestRouter(val mapper: ObjectMapper, val underlying: HttpRouter) {
  inline fun <reified REQ, RESP> route(method: HttpMethod, path: String, noinline handler: suspend (REQ) -> RESP) {
    underlying.route(method, path) {
      val requestJson = request.body().toString("utf8")
      val req = mapper.readValue<REQ>(requestJson, REQ::class.java)
      val resp = handler(req)
      val responseJson = mapper.writeValueAsBytes(resp)
      response(responseJson)
    }
  }

  inline fun <reified REQ, RESP>  get(path: String, noinline handler: suspend (REQ) -> RESP) {
    route(HttpMethod.GET, path, handler)
  }

  inline fun <reified REQ, RESP>  head(path: String, noinline handler: suspend (REQ) -> RESP) {
    route(HttpMethod.HEAD, path, handler)
  }

  inline fun <reified REQ, RESP>  post(path: String, noinline handler: suspend (REQ) -> RESP) {
    route(HttpMethod.POST, path, handler)
  }

  inline fun <reified REQ, RESP>  put(path: String, noinline handler: suspend (REQ) -> RESP) {
    route(HttpMethod.PUT, path, handler)
  }

  inline fun <reified REQ, RESP>  delete(path: String, noinline handler: suspend (REQ) -> RESP) {
    route(HttpMethod.DELETE, path, handler)
  }

  inline fun <reified REQ, RESP>  patch(path: String, noinline handler: suspend (REQ) -> RESP) {
    route(HttpMethod.PATCH, path, handler)
  }

  inline fun <reified REQ, RESP>  options(path: String, noinline handler: suspend (REQ) -> RESP) {
    route(HttpMethod.OPTIONS, path, handler)
  }
}
