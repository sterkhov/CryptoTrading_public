package ru.broom.common_trade.model

class PerfectOrder {
  def this(cost: Float, quantity: Float, sellFlag: Boolean) {
    this()
    this.cost=cost
    this.quantity=quantity
    this.sellFlag=sellFlag
  }

  var cost: Float = _
  var quantity: Float = _
  var sellFlag: Boolean = _

  def getCost: Float = cost
  def getQuantity: Float = quantity
  def isSellFlag: Boolean = sellFlag
  def setCost(cost: Float): Unit = this.cost=cost
  def setQuantity(quantity: Float): Unit = this.quantity=quantity
  def setSellFlag(sellFlag: Boolean): Unit = this.sellFlag=sellFlag
}
