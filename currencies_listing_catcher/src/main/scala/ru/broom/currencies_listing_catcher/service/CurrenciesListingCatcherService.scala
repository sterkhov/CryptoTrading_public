package ru.broom.currencies_listing_catcher.service

import ru.broom.binance_api.service.`trait`.BinanceConnection
import ru.broom.telegram_notifier.service.TelegramNotifier

import scala.collection.mutable.ListBuffer

class CurrenciesListingCatcherService extends BinanceConnection {
  private var currencies: List[String] = _

  def checkNewCurrency(): Unit = {
    try {
      val listBuffer = new ListBuffer[String]
      binanceClient.getAllPrices.forEach(ticker => {

        if (currencies != null)
          if (!currencies.contains(ticker.getSymbol))
            println("Found new currency with symbol - "+ticker.getSymbol+" and cost - "+ticker.getPrice)

        if (currencies == null || currencies.contains(ticker.getSymbol))
          listBuffer.append(ticker.getSymbol)

      })
      currencies=listBuffer.toList
    } catch {
      case e: Exception => {
        e.printStackTrace()
        TelegramNotifier.notifyException(e)
        Thread.sleep(3000)
      }
    }
  }
}
