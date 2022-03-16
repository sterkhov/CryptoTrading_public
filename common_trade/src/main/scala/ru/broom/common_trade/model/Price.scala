package ru.broom.common_trade.model

case class Price(){

  override def toString: String = {
      "{currency: " + currency + "\n" +
      "cost: " + cost + "\n" +
      "timestamp: " + timestamp + "\n" +
      "place: " + place + "}"
  }

  def this(currency: String, cost: Float, timestamp: Long, place: String) {
    this()
    this.currency=currency
    this.cost=cost
    this.timestamp=timestamp
    this.place=place
  }

  var currency: String = _
  var cost: Float = _
  var timestamp: Long = _
  var place: String = _

  def getCurrency = currency
  def getCost = cost
  def getTimestamp = timestamp
  def getPlace = place
  def setCurrency(currency: String) = this.currency=currency
  def setCost(cost: Float) = this.cost=cost
  def setTimestamp(timestamp: Long) = this.timestamp=timestamp
  def setPlace(place: String) = this.place=place
}
