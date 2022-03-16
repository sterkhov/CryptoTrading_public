package ru.broom.binance_kafka_producer.service

import ru.broom.common_kafka.service.CommonKafkaProducer
import ru.broom.common_trade.model.{CallOrderStack, PerfectOrderStack, Price}
import rx.Subscriber

class BinanceKafkaStream extends CommonKafkaProducer with BinanceProducerObserver {
  Thread.sleep(15000)
  def startStream(): Unit = {
    for (topic <- currenciesPriceObserverMap.keySet) {
      createTopic(topic)
      currenciesPriceObserverMap(topic).subscribe(new Subscriber[Price]() {
        override def onCompleted(): Unit = ???
        override def onError(e: Throwable): Unit = ???
        override def onNext(price: Price): Unit = {
          sendEntity(topic, price)
        }
      })
    }
    for (topic <- currenciesAsksOrdersObserverMap.keySet) {
      createTopic(topic)
      currenciesAsksOrdersObserverMap(topic).subscribe(new Subscriber[CallOrderStack]() {
        override def onCompleted(): Unit = ???
        override def onError(e: Throwable): Unit = ???
        override def onNext(callOrderStack: CallOrderStack): Unit = {
          sendEntity(topic, callOrderStack)
        }
      })
    }
    for (topic <- currenciesBidsOrdersObserverMap.keySet) {
      createTopic(topic)
      currenciesBidsOrdersObserverMap(topic).subscribe(new Subscriber[CallOrderStack]() {
        override def onCompleted(): Unit = ???
        override def onError(e: Throwable): Unit = ???
        override def onNext(callOrderStack: CallOrderStack): Unit = {
          sendEntity(topic, callOrderStack)
        }
      })
    }
    for (topic <- currenciesPerfectOrdersObserverMap.keySet) {
      createTopic(topic)
      currenciesPerfectOrdersObserverMap(topic).subscribe(new Subscriber[PerfectOrderStack]() {
        override def onCompleted(): Unit = ???
        override def onError(e: Throwable): Unit = ???
        override def onNext(callOrderStack: PerfectOrderStack): Unit = {
          sendEntity(topic, callOrderStack)
        }
      })
    }
    while(true){
      Thread.sleep(1000)
    }
  }
}
