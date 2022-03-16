package ru.broom.binance_kafka_producer

import ru.broom.binance_kafka_producer.service.BinanceKafkaStream

object BinanceKafkaProducerModule {
  def main(args: Array[String]): Unit ={
    val binanceKafkaStream = new BinanceKafkaStream
    binanceKafkaStream.startStream()
  }
}
