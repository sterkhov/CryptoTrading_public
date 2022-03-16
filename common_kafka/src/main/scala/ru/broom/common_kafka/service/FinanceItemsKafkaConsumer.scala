package ru.broom.common_kafka.service

import java.time.Duration

import com.fasterxml.jackson.databind.ObjectMapper
import ru.broom.common_trade.config.CommonTradeProperties.Currencies.PLAYING_CURRENCIES_ARRAY
import rx.Observable
import scala.collection.JavaConverters._

trait FinanceItemsKafkaConsumer extends CommonKafkaConsumer {
  protected val objectMapper = new ObjectMapper

  private val currenciesPriceConsumer = startSubscribe(PLAYING_CURRENCIES_ARRAY.map(currency => {
    "PRICE_"+currency
  }).toSet.asJava)
  private val currenciesAsksConsumer = startSubscribe(PLAYING_CURRENCIES_ARRAY.map(currency => {
    "ASKS_"+currency
  }).toSet.asJava)
  private val currenciesBidsConsumer = startSubscribe(PLAYING_CURRENCIES_ARRAY.map(currency => {
    "BIDS_"+currency
  }).toSet.asJava)
  private val currenciesPerfectOrdersConsumer = startSubscribe(PLAYING_CURRENCIES_ARRAY.map(currency => {
    "PERFECT_"+currency
  }).toSet.asJava)

  protected val currenciesPriceObservable = Observable.fromCallable(() => {
    currenciesPriceConsumer.poll(Duration.ofSeconds(10))
  }).repeat()
  protected val currenciesAsksObservable = Observable.fromCallable(() => {
    currenciesAsksConsumer.poll(Duration.ofSeconds(10))
  }).repeat()
  protected val currenciesBidsObservable = Observable.fromCallable(() => {
    currenciesBidsConsumer.poll(Duration.ofSeconds(10))
  }).repeat()
  protected val currenciesPerfectOrdersObservable = Observable.fromCallable(() => {
    currenciesPerfectOrdersConsumer.poll(Duration.ofSeconds(10))
  }).repeat()

}
