package ru.broom.neural_networks.strength_measure

import ru.broom.mongo_storage.service.CandleStorage
import ru.broom.neural_networks.strength_measure.model.{LSTMNeuronLayer, OutNeuronLayer}
import scalafx.application.JFXApp
import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene
import scalafx.scene.chart.{LineChart, NumberAxis, XYChart}

import scala.collection.mutable.ListBuffer

// BULLISH  bearish
object Main extends JFXApp {

  val lstmMinuteNeuronLayer = new LSTMNeuronLayer(12, 0.98333)
  val minuteout = new OutNeuronLayer(lstmMinuteNeuronLayer, Array(
    "Moon",
    "Gap up",
    "Strong up",
    "Middle up",
    "Light up",
    "Nothing up",
    "Nothing down",
    "Light down",
    "Middle down",
    "Strong down",
    "Gap down",
    "Deep"
  ))

  val lstmFiftyMinuteNeuronLayer = new LSTMNeuronLayer(12, 0.75)
  val fiftyOut = new OutNeuronLayer(lstmFiftyMinuteNeuronLayer, Array(
    "Moon",
    "Gap up",
    "Strong up",
    "Middle up",
    "Light up",
    "Nothing up",
    "Nothing down",
    "Light down",
    "Middle down",
    "Strong down",
    "Gap down",
    "Deep"
  ))

  val lstmHourNeuronLayer = new LSTMNeuronLayer(12, 0.50)
  val hourOut = new OutNeuronLayer(lstmHourNeuronLayer, Array(
    "Moon",
    "Gap up",
    "Strong up",
    "Middle up",
    "Light up",
    "Nothing up",
    "Nothing down",
    "Light down",
    "Middle down",
    "Strong down",
    "Gap down",
    "Deep"
  ))

  val hourCandles = CandleStorage.findNextCandles("HOUR_INTERVAL", "DOGEUSDT", null,20)
  var listHour = new ListBuffer[(Double, Double)]
  for (position <- hourCandles.indices){
    lstmHourNeuronLayer.input(hourCandles(position).calcDivergencePercent)
    hourOut.flush()
    listHour.append((position*0.25*60, hourOut.getOut))
  }

  val fiftyMinutesCandles = CandleStorage.findNextCandles("FIFTY_MINUTES_INTERVAL", "DOGEUSDT", null,80)
  var listFiftyMinutes = new ListBuffer[(Double, Double)]
  for (position <- fiftyMinutesCandles.indices){
    lstmFiftyMinuteNeuronLayer.input(fiftyMinutesCandles(position).calcDivergencePercent)
    fiftyOut.flush()
    listFiftyMinutes.append((position*0.25*15, fiftyOut.getOut))
  }

  val oneMinuteCandles = CandleStorage.findNextCandles("ONE_MINUTE_INTERVAL", "DOGEUSDT", null,2600)
  var listOneMinutes = new ListBuffer[(Double, Double)]
  for (position <- oneMinuteCandles.indices){
    lstmMinuteNeuronLayer.input(oneMinuteCandles(position).calcDivergencePercent)
    minuteout.flush()
    listOneMinutes.append((position*0.25, minuteout.getOut))
  }

  stage = new JFXApp.PrimaryStage {
    title = "Line Chart Example"
    scene = new Scene {
      root = {

        val xAxis = NumberAxis("Values for X-Axis", 0, 600, 0.25)
        val yAxis = NumberAxis("Values for Y-Axis", -7, 7, 1)

        // Helper function to convert a tuple to `XYChart.Data`
        val toChartData = (xy: (Double, Double)) => XYChart.Data[Number, Number](xy._1, xy._2)

        val series1 = new XYChart.Series[Number, Number] {
          name = "Series 1"
          data = listFiftyMinutes.toSeq.map(toChartData)
        }

        val series2 = new XYChart.Series[Number, Number] {
          name = "Series 2"
          data = listOneMinutes.toSeq.map(toChartData)
        }

        val series3 = new XYChart.Series[Number, Number] {
          name = "Series 3"
          data = listHour.toSeq.map(toChartData)
        }

        new LineChart[Number, Number](xAxis, yAxis, ObservableBuffer(series1, series2, series3))
      }
    }
  }

}
