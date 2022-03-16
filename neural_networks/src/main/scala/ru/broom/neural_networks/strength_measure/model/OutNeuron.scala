package ru.broom.neural_networks.strength_measure.model

abstract class OutNeuron(val outValue: String) {
  var activation: Double = 0.0d
  def clean(): Unit = activation = 0.0d
  def input(value: Double): Unit = activation = activation + sigmoid(value)

  def sigmoid(input: Double): Double
}
