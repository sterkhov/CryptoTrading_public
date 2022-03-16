package ru.broom.trading_core

import ru.broom.trading_core.service.CandleService

object TradingCoreModule {
  def main(args: Array[String]): Unit = {
    val candleService = new CandleService()
    Thread.sleep(10000)
    while (true) {
      Thread.sleep(1000)
    }
  }
}
