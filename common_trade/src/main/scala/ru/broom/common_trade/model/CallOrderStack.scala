package ru.broom.common_trade.model

import java.util

case class CallOrderStack() {
  def this(timestamp: Long, orderBookList: util.List[CallOrder], currency: String, place: String) {
    this()
    this.timestamp = timestamp
    this.orderBookList=orderBookList
    this.currency=currency
    this.place=place
  }

  var timestamp:Long = _
  var orderBookList: util.List[CallOrder] = _
  var place: String = _
  var currency: String = _

  def getTimestamp: Long = timestamp
  def getOrderBookList: util.List[CallOrder] = orderBookList
  def getPlace: String = place
  def getCurrency: String = currency
  def setTimestamp(timestamp: Long): Unit = this.timestamp = timestamp
  def setOrderBookList(orderBookList: util.List[CallOrder]): Unit = this.orderBookList=orderBookList
  def setPlace(place: String): Unit = this.place=place
  def setCurrency(currency: String): Unit = this.currency=currency
}
