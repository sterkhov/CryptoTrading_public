package ru.broom.neural_networks.trend_line.model

import scala.collection.mutable


class CostOutNeuronLayer(beforeLayer: CostLSTMNeuronLayer, layerSize: Int) {
  val outNeurons: Array[CostOutNeuron] = {
    val outNeuronArray = new Array[CostOutNeuron](layerSize)
    for (position <- 0 until layerSize) {
      val outNeuron = new CostOutNeuron(){
        override def sigmoid(input: Double): Double = {
          val high = 6-position*0.05
          val lower = high-0.05

          val roundHigh = Math.round(high * 100.0) / 100.0
          val roundLower = Math.round(lower * 100.0) / 100.0

          if (Math.abs(input)>6)
            println("MAX VALUE")

          if (input<0){
            if (roundLower<input && input<=roundHigh) {
              input
            } else {
              0.0
            }
          } else {
            if (roundLower<=input && input<roundHigh) {
              input
            } else {
              0.0
            }
          }
        }
      }
      outNeuronArray(position) = outNeuron
    }
    outNeuronArray
  }
  val weightMap: Map[(Int, Int), Double] = {
    val hashMap = new mutable.HashMap[(Int, Int), Double]()
    for (pos1 <- beforeLayer.lstmNeurons.indices) {
      val lstmNeuron = beforeLayer.lstmNeurons(pos1)
      for (pos2 <- outNeurons.indices) {
        val outNeuron = outNeurons(pos2)

        val weight = 1.0 - (Math.abs(pos1 - pos2) * 1.0 / outNeurons.length)
        hashMap.put((lstmNeuron.hashCode(), outNeuron.hashCode()), weight)
      }
    }
    hashMap.toMap
  }

  private def cleanOut(): Unit = outNeurons.foreach(_.clean())

  def flush(): Unit ={
    cleanOut()
    for (outNeuron <- outNeurons) {
      val activation = beforeLayer.lstmNeurons.map(
        lstm => {
          val weight = weightMap(lstm.hashCode(), outNeuron.hashCode())
          lstm.activationValue * weight
        }
      ).sum
      outNeuron.input(activation)

    }
  }

  def getOut: Double = {
    var maxActivated = 0.0d
    var maxActivatedNeuron: CostOutNeuron = null
    for (outNeuron <- outNeurons){
      val absActivated = Math.abs(outNeuron.activation)
      if (maxActivated<absActivated){
        maxActivated = absActivated
        maxActivatedNeuron = outNeuron
      }
    }
    if (maxActivatedNeuron!=null) maxActivatedNeuron.activation else 0
  }
}
