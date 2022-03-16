package ru.broom.neural_networks.trend_line.model

abstract class CostLSTMNeuron(private val lastActivationTransparency: Double) {
  var activationValue: Double = 0.0d
  def input(value: Double): Unit = activationValue = sigmoid(value) + activationValue*lastActivationTransparency
  def sigmoid(value: Double): Double
}
