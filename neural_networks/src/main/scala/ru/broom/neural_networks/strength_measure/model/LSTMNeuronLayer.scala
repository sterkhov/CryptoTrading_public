package ru.broom.neural_networks.strength_measure.model

class LSTMNeuronLayer(layerSize: Int, lastActivationTransparency: Double) {
  val lstmNeurons = {
    val layerArray = new Array[LSTMNeuron](layerSize)
    for (position <- 0 until layerSize) {
      val neuron = new LSTMNeuron(lastActivationTransparency){
        override def sigmoid(input: Double): Double = {
          val high = 6-position
          val lower = 6-position-1
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