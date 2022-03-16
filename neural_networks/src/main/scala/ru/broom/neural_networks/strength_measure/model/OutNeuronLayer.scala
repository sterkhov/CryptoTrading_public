package ru.broom.neural_networks.strength_measure.model

import scala.collection.mutable


class OutNeuronLayer(beforeLayer: LSTMNeuronLayer, outValues: Array[String]) {
  val outNeurons: Array[OutNeuron] = {
    val outNeuronArray = new Array[OutNeuron](outValues.length)
    for (position <- outValues.indices) {
      val outNeuron = new OutNeuron(outValues(position)){
        override def sigmoid(input: Double): Double = {
          val high = 6-position*2
          val lower = 6-position*2-2
          if (input<0){
            if (lower<input && input<=high) {
              input
            } else {
              0.0
            }
          } else {
            if (lower<=input && input<high) {
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
          lstm.activationValue * weightMap(lstm.hashCode(), outNeuron.hashCode())
        }
      ).sum
      outNeuron.input(activation)

    }
  }

  def getOut: Double = {
    var maxActivated = 0.0d
    var maxActivatedNeuron: OutNeuron = null
    for (outNeuron <- outNeurons){
      val absActivated = Math.abs(outNeuron.activation)
      if (maxActivated<=absActivated){
        maxActivated = absActivated
        maxActivatedNeuron = outNeuron
      }
    }
    if (maxActivatedNeuron!=null) maxActivatedNeuron.activation else 0
  }
}
