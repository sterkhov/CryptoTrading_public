package ru.broom.neural_networks.trend_line

object Main {
  def main(args: Array[String]): Unit = {
    new TrendLineNetwork()
    while(true){
      Thread.sleep(1000)
    }
  }
}
