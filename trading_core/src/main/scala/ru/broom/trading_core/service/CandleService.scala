package ru.broom.trading_core.service

import java.util.Date

import com.fasterxml.jackson.databind.JsonNode
import org.apache.kafka.clients.consumer.ConsumerRecords
import ru.broom.common_kafka.service.FinanceItemsKafkaConsumer
import ru.broom.common_trade.common.DateTimeUtil
import ru.broom.common_trade.model.{CallOrderStack, Candle, PerfectOrderStack, Price}
import rx.Subscriber

import scala.collection.mutable
import ru.broom.common_trade.config.CommonTradeProperties.Currencies.PLAYING_CURRENCIES_ARRAY
import ru.broom.common_trade.constants.Constants
import ru.broom.mongo_storage.service.CandleStorage

class CandleService extends FinanceItemsKafkaConsumer {

  @volatile var oneMinuteCandleMap: mutable.HashMap[String, Candle] = createAroundCandleMap(Constants.Candle.Intervals.ONE_MINUTE_INTERVAL)
  @volatile var fiftyMinutesCandleMap: mutable.HashMap[String, Candle] = createAroundCandleMap(Constants.Candle.Intervals.FIFTY_MINUTES_INTERVAL)
  @volatile var hourCandleMap: mutable.HashMap[String, Candle] = createAroundCandleMap(Constants.Candle.Intervals.HOUR_INTERVAL)

  new Thread(() => {
    while (true) {
      try {
        saveCandlesAndCreateNewIfPeriodEnded(Constants.Candle.Intervals.ONE_MINUTE_INTERVAL)
        saveCandlesAndCreateNewIfPeriodEnded(Constants.Candle.Intervals.FIFTY_MINUTES_INTERVAL)
        saveCandlesAndCreateNewIfPeriodEnded(Constants.Candle.Intervals.HOUR_INTERVAL)
      } catch {
        case e: Exception => {
          //TelegramNotifier.notifyException(e)
          Thread.sleep(3000)
        }
      }
      Thread.sleep(300)
    }
  }).start()

  new Thread(() => {
    Thread.sleep(5000)
    currenciesPriceObservable.subscribe(new Subscriber[ConsumerRecords[String, JsonNode]]() {
      override def onCompleted(): Unit = ???
      override def onError(e: Throwable): Unit = ???
      override def onNext(consumerRecords: ConsumerRecords[String, JsonNode]): Unit = {
        if (consumerRecords!=null && !consumerRecords.isEmpty){
          try {
            consumerRecords.forEach(e=>{
              val price = objectMapper.convertValue(e.value(), classOf[Price])
              if (oneMinuteCandleMap(price.currency).isCorrectTimestamp(price.getTimestamp)) oneMinuteCandleMap(price.currency).addPrice(price)
              if (fiftyMinutesCandleMap(price.currency).isCorrectTimestamp(price.getTimestamp)) fiftyMinutesCandleMap(price.currency).addPrice(price)
              if (hourCandleMap(price.currency).isCorrectTimestamp(price.getTimestamp)) hourCandleMap(price.currency).addPrice(price)
            })
          } catch {
            case e: Exception => {
             // TelegramNotifier.notifyException(e)
              Thread.sleep(3000)
            }
          }
        }
      }
    })
  }).start()

  new Thread(() => {
    Thread.sleep(5000)
    currenciesAsksObservable.subscribe(new Subscriber[ConsumerRecords[String, JsonNode]]() {
      override def onCompleted(): Unit = ???
      override def onError(e: Throwable): Unit = ???
      override def onNext(consumerRecords: ConsumerRecords[String, JsonNode]): Unit = {
        if (consumerRecords!=null && !consumerRecords.isEmpty){
          try {
            consumerRecords.forEach(e=>{
              val asks = objectMapper.convertValue(e.value(), classOf[CallOrderStack])
              if (oneMinuteCandleMap(asks.currency).isCorrectTimestamp(asks.getTimestamp)) oneMinuteCandleMap(asks.currency).addAsksStack(asks)
              if (fiftyMinutesCandleMap(asks.currency).isCorrectTimestamp(asks.getTimestamp)) fiftyMinutesCandleMap(asks.currency).addAsksStack(asks)
              if (hourCandleMap(asks.currency).isCorrectTimestamp(asks.getTimestamp)) hourCandleMap(asks.currency).addAsksStack(asks)
            })
          } catch {
            case e: Exception => {
            //  TelegramNotifier.notifyException(e)
              Thread.sleep(3000)
            }
          }
        }
      }
    })
  }).start()

  new Thread(() => {
    Thread.sleep(5000)
    currenciesBidsObservable.subscribe(new Subscriber[ConsumerRecords[String, JsonNode]]() {
      override def onCompleted(): Unit = ???
      override def onError(e: Throwable): Unit = ???
      override def onNext(consumerRecords: ConsumerRecords[String, JsonNode]): Unit = {
        if (consumerRecords!=null && !consumerRecords.isEmpty){
          try {
            consumerRecords.forEach(e=>{
              val bids = objectMapper.convertValue(e.value(), classOf[CallOrderStack])
              if (oneMinuteCandleMap(bids.currency).isCorrectTimestamp(bids.getTimestamp)) oneMinuteCandleMap(bids.currency).addBidsStack(bids)
              if (fiftyMinutesCandleMap(bids.currency).isCorrectTimestamp(bids.getTimestamp)) fiftyMinutesCandleMap(bids.currency).addBidsStack(bids)
              if (hourCandleMap(bids.currency).isCorrectTimestamp(bids.getTimestamp)) hourCandleMap(bids.currency).addBidsStack(bids)
            })
          } catch {
            case e: Exception => {
            //  TelegramNotifier.notifyException(e)
              Thread.sleep(3000)
            }
          }
        }
      }
    })
  }).start()

  new Thread(() => {
    Thread.sleep(5000)
    currenciesPerfectOrdersObservable.subscribe(new Subscriber[ConsumerRecords[String, JsonNode]]() {
      override def onCompleted(): Unit = ???
      override def onError(e: Throwable): Unit = ???
      override def onNext(consumerRecords: ConsumerRecords[String, JsonNode]): Unit = {
        if (consumerRecords!=null && !consumerRecords.isEmpty){
          try {
            consumerRecords.forEach(e=>{
              val perfect = objectMapper.convertValue(e.value(), classOf[PerfectOrderStack])
              if (oneMinuteCandleMap(perfect.currency).isCorrectTimestamp(perfect.getEndTime)) oneMinuteCandleMap(perfect.currency).addPerfectOrderStack(perfect)
              if (fiftyMinutesCandleMap(perfect.currency).isCorrectTimestamp(perfect.getEndTime)) fiftyMinutesCandleMap(perfect.currency).addPerfectOrderStack(perfect)
              if (hourCandleMap(perfect.currency).isCorrectTimestamp(perfect.getEndTime)) hourCandleMap(perfect.currency).addPerfectOrderStack(perfect)
            })
          } catch {
            case e: Exception => {
            //  TelegramNotifier.notifyException(e)
              Thread.sleep(3000)
            }
          }
        }
      }
    })
  }).start()

  private def saveCandleMap(dbName:String, candleMap: Map[String, Candle]): Unit = {
    for ((currency, candle) <- candleMap){
      CandleStorage.saveCandle(dbName, currency, candle)
    }
  }

  private def saveCandlesAndCreateNewIfPeriodEnded(interval: (Int,Int,Int)): Unit ={
    interval match {
      case Constants.Candle.Intervals.ONE_MINUTE_INTERVAL => {
        if (!oneMinuteCandleMap.head._2.isCorrectTimestamp(System.currentTimeMillis())) {
          val cloneMap = oneMinuteCandleMap.toMap
          oneMinuteCandleMap = createAroundCandleMap(Constants.Candle.Intervals.ONE_MINUTE_INTERVAL)
          saveCandleMap("ONE_MINUTE_INTERVAL", cloneMap)

        }
      }
      case Constants.Candle.Intervals.FIFTY_MINUTES_INTERVAL => {
        if (!fiftyMinutesCandleMap.head._2.isCorrectTimestamp(System.currentTimeMillis())) {
          val cloneMap = fiftyMinutesCandleMap.toMap
          fiftyMinutesCandleMap = createAroundCandleMap(Constants.Candle.Intervals.FIFTY_MINUTES_INTERVAL)
          saveCandleMap("FIFTY_MINUTES_INTERVAL", cloneMap)
        }
      }
      case Constants.Candle.Intervals.HOUR_INTERVAL => {
        if (!hourCandleMap.head._2.isCorrectTimestamp(System.currentTimeMillis())) {
          val cloneMap = hourCandleMap.toMap
          hourCandleMap = createAroundCandleMap(Constants.Candle.Intervals.HOUR_INTERVAL)
          saveCandleMap("HOUR_INTERVAL", cloneMap)
        }
      }
    }
  }

  private def createAroundCandleMap(interval: (Int, Int, Int)): mutable.HashMap[String, Candle] = {
    val aroundDates = DateTimeUtil.createAroundDates(new Date(System.currentTimeMillis()), interval)
    val candleMap = createCandleMap(aroundDates._1, aroundDates._2)
    println("Created new candles " + aroundDates._1 + " " + aroundDates._2)
    candleMap
  }

  private def createCandleMap(openDate: Date, closeDate: Date): mutable.HashMap[String, Candle] = {
    val hashMap = new mutable.HashMap[String, Candle]
    for (currency <- PLAYING_CURRENCIES_ARRAY){
      hashMap.put(currency, new Candle(currency, openDate.getTime, closeDate.getTime, "BINANCE"))
    }
    hashMap
  }

}
