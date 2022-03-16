package ru.broom.neural_networks.trend_line.model

class CostLSTMNeuronLayer(layerSize: Int, lastActivationTransparency: Double) {
  val lstmNeurons = {
    val layerArray = new Array[CostLSTMNeuron](layerSize)
    for (position <- 0 until layerSize) {
      val neuron = new CostLSTMNeuron(lastActivationTransparency){
        override def sigmoid(input: Double): Double = {
          val high = 6-position*0.05
          val lower = high-0.05

          val roundHigh = Math.round(high * 100.0) / 100.0
          val roundLower = Math.round(lower * 100.0) / 100.0

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
      layerArray(position) = neuron
    }
    layerArray
  }
  def input(value: Double): Unit = {
    for (neuron <- lstmNeurons){
      neuron.input(value)
    }
  }

  def forget: Unit = {
    for (neuron <- lstmNeurons){
      neuron.activationValue = 0
    }
  }
}