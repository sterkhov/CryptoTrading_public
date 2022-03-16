package ru.broom.mongo_storage.service

import org.junit.runner.RunWith
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CandleStorageTest extends AnyFunSuite {
  test("") {
    val list = CandleStorage.findNextCandles("ONE_MINUTE_INTERVAL", "DOGEUSDT", null,100)
    println("TEST")
  }
}
