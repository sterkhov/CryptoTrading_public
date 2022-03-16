package ru.broom.neural_networks.trend_line

import com.fasterxml.jackson.databind.JsonNode
import org.apache.kafka.clients.consumer.ConsumerRecords
import ru.broom.common_kafka.service.FinanceItemsKafkaConsumer
import ru.broom.common_trade.config.CommonTradeProperties.Currencies.PLAYING_CURRENCIES_ARRAY
import ru.broom.common_trade.model.Price
import ru.broom.neural_networks.trend_line.model.{CostLSTMNeuronLayer, CostOutNeuronLayer, PocketOrder}
import rx.Subscriber

import scala.collection.mutable

class TrendLineNetwork extends FinanceItemsKafkaConsumer {
  @volatile var currenciesStrengthMap = createCurrenciesStrengthMap()
  @volatile var lastPriceMap = createLastPriceMap()

  private def createLastPriceMap(): mutable.HashMap[String, Float] = {
    val hashMap = new  mutable.HashMap[String, Float]
    for (currency <- PLAYING_CURRENCIES_ARRAY){
      hashMap.put(currency, 0f)
    }
    hashMap
  }

  private def createCurrenciesStrengthMap(): mutable.HashMap[String, (CostLSTMNeuronLayer, CostOutNeuronLayer)] = {
    val hashMap = new mutable.HashMap[String, (CostLSTMNeuronLayer, CostOutNeuronLayer)]
    for (currency <- PLAYING_CURRENCIES_ARRAY){
      val lstmMinuteNeuronLayer = new CostLSTMNeuronLayer(240, 0.9833)
      val minuteout = new CostOutNeuronLayer(lstmMinuteNeuronLayer, 240)
      hashMap.put(currency, (lstmMinuteNeuronLayer, minuteout))
    }
    hashMap
  }


  @volatile var closeOrders = new mutable.ListBuffer[PocketOrder]
  @volatile var orderMap = new mutable.HashMap[String, PocketOrder]()

  new Thread(() => {
    Thread.sleep(5000)
    currenciesPriceObservable.subscribe(new Subscriber[ConsumerRecords[String, JsonNode]]() {
      override def onCompleted(): Unit = ???
      override def onError(e: Throwable): Unit = ???
      override def onNext(consumerRecords: ConsumerRecords[String, JsonNode]): Unit = {
        if (consumerRecords!=null && !consumerRecords.isEmpty){
          try {
            consumerRecords.forEach(e=>{
                new Thread(() => {
                  val price = objectMapper.convertValue(e.value(), classOf[Price])
                  val skipInterval = System.currentTimeMillis() - price.getTimestamp
                  if (skipInterval<2000) {
                    val tuple2 = currenciesStrengthMap(price.getCurrency)
                    val lastPrice = lastPriceMap(price.getCurrency)
                    val divPercent = (price.getCost / (lastPrice / 100f)) - 100f
                    if (lastPrice != 0f) {
                      tuple2._1.input(divPercent)
                      tuple2._2.flush()
                      val outActivaton = tuple2._2.getOut
                      val currency = price.getCurrency
                      if (outActivaton != 0) {
                        //println(currency, outActivaton)

                        if (orderMap.get(currency).isDefined && orderMap(currency).closed) {
                          closeOrders.append(orderMap(currency))
                          orderMap.remove(currency)
                        }

                        if (orderMap.get(currency).isDefined) {
                          //println(currency, outActivaton)
                          orderMap(currency).nextCost(price.getCost)
                          if (orderMap(currency).closed) {
                            closeOrders.append(orderMap(currency))
                            orderMap.remove(currency)
                          }
                        }

                        if (outActivaton > 0.025 && !orderMap.get(currency).isDefined) {
                          orderMap.put(currency, PocketOrder(currency, price.getCost, 20 / price.getCost))
                        } else if (orderMap.get(currency).isDefined && outActivaton < 0 && orderMap(currency).profit >= 0.01) {
                          orderMap(currency).closeOrder(price.getCost)
                          closeOrders.append(orderMap(currency))
                          orderMap.remove(currency)
                        } else if (orderMap.get(currency).isDefined && orderMap(currency).profit > 0.01) {
                          println(currency, orderMap(currency).profit)
                          //                        orderMap(currency).closeOrder(price.getCost)
                          //                        closeOrders.append(orderMap(currency))
                          //                        orderMap.remove(currency)
                        }

                      }
                    }
                    lastPriceMap(price.getCurrency) = price.getCost
                  }
                }).start()
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

}
