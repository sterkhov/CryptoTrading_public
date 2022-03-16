package ru.broom.binance_api.config

import java.util.Properties

object BinanceApiProperties {
  private val properties = new Properties()
  properties.load(getClass.getResource("/binance-api.properties").openStream())
  object Authorisation {
    val API_KEY = properties.getProperty("binance.apikey")
    val SECRET = properties.getProperty("binance.secret")
  }
}
