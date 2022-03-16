package ru.broom.binance_kafka_producer.config

import java.util.Properties

object BinanceKafkaProducerProperties {
  private val properties = new Properties()
  properties.load(getClass.getResource("/binance-kafka-producer.properties").openStream())
  object AskBinance {
    val PRICE_ASK_TIMEOUT_MILLISECONDS: Int = properties.getProperty("binance.ask.price.timeout").toInt
    val CALL_ORDER_ASK_TIMEOUT_MILLISECONDS: Int = properties.getProperty("binance.ask.callorder.timeout").toInt
    val CALL_ORDER_ASK_STACK_COUNT: Int = properties.getProperty("binance.ask.callorder.stack").toInt
    val PERFECT_ORDER_ASK_TIMEOUT_MILLISECONDS: Int = properties.getProperty("binance.ask.perfectorder.timeout").toInt
    val PERFECT_ORDER_ASK_STACK_COUNT: Int = properties.getProperty("binance.ask.perfectorder.stack").toInt
  }
}
