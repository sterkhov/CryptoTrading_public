package ru.broom.neural_networks.trend_line.model

abstract class CostOutNeuron() {
  var activation: Double = 0.0d
  def clean(): Unit = activation = 0.0d
  def input(value: Double): Unit = activation = activation + sigmoid(value)
  def sigmoid(input: Double): Double
}
