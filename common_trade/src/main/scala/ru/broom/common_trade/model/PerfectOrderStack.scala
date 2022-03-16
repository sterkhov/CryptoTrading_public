package ru.broom.common_trade.model

import java.util

case class PerfectOrderStack() {
  def this(startTime: Long, endTime: Long, perfectOrderList: util.List[PerfectOrder], currency: String, place: String) {
    this()
    this.startTime = startTime
    this.endTime = endTime
    this.perfectOrderList=perfectOrderList
    this.currency=currency
    this.place=place
  }

  var startTime:Long = _
  var endTime:Long = _
  var perfectOrderList: util.List[PerfectOrder] = _
  var currency: String = _
  var place: String = _

  def getStartTime: Long = startTime
  def getEndTime: Long = endTime
  def getPerfectOrderList: util.List[PerfectOrder] = perfectOrderList
  def getCurrency: String = currency
  def getPlace: String = place
  def setStartTime(startTime: Long): Unit = this.startTime = startTime
  def setEndTime(endTime: Long): Unit = this.endTime = endTime
  def setPerfectOrderList(perfectOrderList: util.List[PerfectOrder]): Unit = this.perfectOrderList=perfectOrderList
  def setPlace(place: String): Unit = this.place=place
  def setCurrency(currency: String): Unit = this.currency=currency
}
