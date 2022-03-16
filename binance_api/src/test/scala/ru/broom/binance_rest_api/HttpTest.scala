package ru.broom.binance_rest_api

import org.junit.runner.RunWith
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.junit.JUnitRunner
import sttp.client3._

@RunWith(classOf[JUnitRunner])
class HttpTest extends AnyFunSuite {
  test("") {
    val request = basicRequest.get(uri"https://api.binance.com/sapi/v1/system/status")

    val backend = HttpURLConnectionBackend()
    val response = request.send(backend)

    // response.header(...): Option[String]
    println(response.header("Content-Length"))

    // response.body: by default read into an Either[String, String] to indicate failure or success
    println(response.body)
  }

}
