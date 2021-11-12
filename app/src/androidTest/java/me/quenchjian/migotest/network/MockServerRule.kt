package me.quenchjian.migotest.network

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.rules.ExternalResource

class MockServerRule : ExternalResource() {

  private val server = MockWebServer()
  private var dispatcher: Dispatcher? = null

  override fun before() {
    server.start(8080)
    server.dispatcher = CustomDispatcher().also { dispatcher = it }
  }

  override fun after() {
    server.shutdown()
    dispatcher = null
  }

  private class CustomDispatcher : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse {
      return when (request.path) {
        "/status" -> MockResponse().setBody("""{"status":"200","message":"OK"}""")
        else -> MockResponse()
      }
    }
  }
}