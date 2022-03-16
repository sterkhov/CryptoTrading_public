package ru.broom.common_trade.model

case class CallOrder() {
  def this(cost: Float, quantity: Float) {
    this()
    this.cost=cost
    this.quantity=quantity
  }

  var cost: Float = _
  var quantity: Float = _

  def getCost: Float = cost
  def getQuantity: Float = quantity
  def setCost(cost: Float): Unit = this.cost=cost
  def setQuantity(quantity: Float): Unit = this.quantity=quantity
}
