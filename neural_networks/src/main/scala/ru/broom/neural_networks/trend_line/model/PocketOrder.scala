package ru.broom.neural_networks.trend_line.model

case class PocketOrder(currency: String, buyCost: Float, count: Float) {
  println("Buy "+count+" "+currency+" as "+buyCost)

  val stopLoss = buyCost - buyCost*0.01
  var sellCost = 0f
  var closed = false
  var profit = 0f


  def closeOrder(sellCost: Float): Unit ={
    closed = true
    val sellProfit = (sellCost * count) - (sellCost * count * 0.001f)
    val buyLoss = (buyCost * count) + (buyCost * count * 0.001f)
    profit =  sellProfit - buyLoss
    println("Sell "+count+" "+currency+" as "+sellCost+". Profit "+profit)
  }

  def nextCost(cost: Float): Unit = {
    val buyLoss = (buyCost * count) + (buyCost * count * 0.001f)
    val sellProfit = (cost * count) - (cost * count * 0.001f)
    profit =  sellProfit - buyLoss

    if (cost<=stopLoss){
      println("Closed by StopLoss")
      closeOrder(cost)
    }
  }
}
