package ru.broom.binance_kafka_producer.service

import com.binance.api.client.domain.market.{OrderBook, TickerPrice}
import ru.broom.binance_api.service.`trait`.BinanceConnection
import ru.broom.binance_kafka_producer.config.BinanceKafkaProducerProperties.AskBinance._
import ru.broom.common_trade.model.{CallOrder, CallOrderStack, PerfectOrder, PerfectOrderStack, Price}
import ru.broom.common_trade.config.CommonTradeProperties.Currencies.PLAYING_CURRENCIES_ARRAY
import ru.broom.telegram_notifier.service.TelegramNotifier

import scala.collection.mutable.ListBuffer
import rx.subjects.BehaviorSubject

import scala.collection.JavaConverters._

trait BinanceProducerObserver extends BinanceConnection {

  protected val currenciesPriceObserverMap: Map[String, BehaviorSubject[Price]] =
    PLAYING_CURRENCIES_ARRAY.map(currency=>{("PRICE_"+currency, BehaviorSubject.create[Price]())}).toMap

  protected val currenciesAsksOrdersObserverMap: Map[String, BehaviorSubject[CallOrderStack]] =
    PLAYING_CURRENCIES_ARRAY.map(currency=>{("ASKS_"+currency, BehaviorSubject.create[CallOrderStack]())}).toMap

  protected val currenciesBidsOrdersObserverMap: Map[String, BehaviorSubject[CallOrderStack]] =
    PLAYING_CURRENCIES_ARRAY.map(currency=>{("BIDS_"+currency, BehaviorSubject.create[CallOrderStack]())}).toMap

  protected val currenciesPerfectOrdersObserverMap: Map[String, BehaviorSubject[PerfectOrderStack]] =
    PLAYING_CURRENCIES_ARRAY.map(currency=>{("PERFECT_"+currency, BehaviorSubject.create[PerfectOrderStack]())}).toMap

  new Thread(() => {
    Thread.sleep(10000)
    while(true) {
      try {
        val binanceServerTime = binanceClient.getServerTime
        for (ticker <- filteringCurrencies(binanceClient.getAllPrices)) {
          val behavoirSubject = currenciesPriceObserverMap.get("PRICE_"+ticker.getSymbol).get
          behavoirSubject.onNext(new Price(ticker.getSymbol, ticker.getPrice.toFloat, binanceServerTime, "BINANCE"))
        }
        Thread.sleep(PRICE_ASK_TIMEOUT_MILLISECONDS)
      } catch {
        case e: Exception => {
          e.printStackTrace()
          TelegramNotifier.notifyException(e)
          Thread.sleep(3000)
        }
      }
    }
  }).start()

  new Thread(() => {
    Thread.sleep(10000)
    while(true) {
      try {
        val binanceServerTime = binanceClient.getServerTime
        for (currency <- PLAYING_CURRENCIES_ARRAY){
          new Thread(() => {
            val orderBook: OrderBook = binanceClient.getOrderBook(currency, CALL_ORDER_ASK_STACK_COUNT)
            val askBehaviorSubject = currenciesAsksOrdersObserverMap("ASKS_" + currency)
            val bidsBehaviorSubject = currenciesBidsOrdersObserverMap("BIDS_" + currency)
            val asksList = new ListBuffer[CallOrder]
            orderBook.getAsks.forEach(ask => {
              asksList.append(new CallOrder(ask.getPrice.toFloat, ask.getQty.toFloat))
            })
            val bidsList = new ListBuffer[CallOrder]
            orderBook.getBids.forEach(bids => {
              bidsList.append(new CallOrder(bids.getPrice.toFloat, bids.getQty.toFloat))
            })
            askBehaviorSubject.onNext(new CallOrderStack(binanceServerTime, asksList.toList.asJava, currency, "BINANCE"))
            bidsBehaviorSubject.onNext(new CallOrderStack(binanceServerTime, bidsList.toList.asJava, currency, "BINANCE"))
          }).start()
        }
        Thread.sleep(CALL_ORDER_ASK_TIMEOUT_MILLISECONDS)
      } catch {
        case e: Exception => {
          e.printStackTrace()
//          TelegramNotifier.notifyException(e)
          Thread.sleep(3000)
        }
      }
    }
  }).start()

  new Thread(() => {
    Thread.sleep(10000)
    while(true) {
      try {
        for (currency <- PLAYING_CURRENCIES_ARRAY){
          val startTime: Long = System.currentTimeMillis()-PERFECT_ORDER_ASK_TIMEOUT_MILLISECONDS
          val endTime: Long = System.currentTimeMillis()
          new Thread(() => {
            val aggTrades = binanceClient.getAggTrades(currency,null,PERFECT_ORDER_ASK_STACK_COUNT, startTime, endTime)
            val perfectOrders = new ListBuffer[PerfectOrder]
            aggTrades.forEach(trade=>{
              perfectOrders.append(new PerfectOrder(trade.getPrice.toFloat,trade.getQuantity.toFloat,trade.isBuyerMaker))
            })
            val perfectOrdersBehaviorSubject = currenciesPerfectOrdersObserverMap("PERFECT_" + currency)
            perfectOrdersBehaviorSubject.onNext(new PerfectOrderStack(startTime, endTime, perfectOrders.toList.asJava, currency, "BINANCE"))
          }).start()
        }
        Thread.sleep(PERFECT_ORDER_ASK_TIMEOUT_MILLISECONDS)
      } catch {
        case e: Exception => {
          e.printStackTrace()
//          TelegramNotifier.notifyException(e)
          Thread.sleep(3000)
        }
      }
    }
  }).start()

  private def filteringCurrencies(tickers: java.util.List[TickerPrice]): List[TickerPrice] = {
    val listBuffer = new ListBuffer[TickerPrice]
    tickers.forEach(ticker=>{
      if (PLAYING_CURRENCIES_ARRAY.contains(ticker.getSymbol))
        listBuffer.addOne(ticker)
    })
    listBuffer.toList
  }
}



