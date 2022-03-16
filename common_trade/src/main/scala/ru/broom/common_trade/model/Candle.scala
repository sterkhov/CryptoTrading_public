package ru.broom.common_trade.model

import java.util

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import ru.broom.common_trade.config.CommonTradeProperties.Candle._

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters._

case class Candle() {
  def this(currency: String, openTime: Long, closeTime: Long, place: String){
    this()
    this.currency=currency
    this.openTime=openTime
    this.closeTime=closeTime
    this.place=place
  }

  @BsonId
  var objectId: ObjectId = _
  def setObjectId(objectId: ObjectId): Unit = this.objectId=objectId
  def getObjectId: ObjectId = objectId

  var currency: String = _
  var place: String = _
  var openTime: Long = 0L
  var closeTime: Long = 0L
  var openPrice: Price = _
  var closePrice: Price = _
  var minPrice: Price = _
  var maxPrice: Price = _
  var listAsksStack = new ListBuffer[CallOrderStack]
  var listBidsStack = new ListBuffer[CallOrderStack]
  var listPerfectOrderStack = new ListBuffer[PerfectOrderStack]
  var listPrice = new ListBuffer[Price]

  def setCurrency(currency: String): Unit = this.currency=currency
  def setPlace(place: String): Unit = this.place=place
  def setOpenTime(openTime: Long): Unit = this.openTime=openTime
  def setCloseTime(closeTime: Long): Unit = this.closeTime=closeTime
  def setOpenPrice(openPrice: Price): Unit = this.openPrice=openPrice
  def setClosePrice(closePrice: Price): Unit = this.closePrice=closePrice
  def setMinPrice(minPrice: Price): Unit = this.minPrice=minPrice
  def setMaxPrice(maxPrice: Price): Unit = this.maxPrice=maxPrice
  def setListAsksStack(listAsksStack: util.List[CallOrderStack]): Unit =
    this.listAsksStack=new ListBuffer[CallOrderStack].addAll(listAsksStack.asScala)
  def setListBidsStack(listBidsStack: util.List[CallOrderStack]): Unit =
    this.listBidsStack=new ListBuffer[CallOrderStack].addAll(listBidsStack.asScala)
  def setListPerfectOrderStack(listPerfectOrderStack: util.List[PerfectOrderStack]): Unit =
    this.listPerfectOrderStack=new ListBuffer[PerfectOrderStack].addAll(listPerfectOrderStack.asScala)
  def setListPriceStack(listPrice: util.List[Price]): Unit =
    this.listPrice=new ListBuffer[Price].addAll(listPrice.asScala)

  def getCurrency: String = currency
  def getPlace: String = place
  def getOpenTime: Long = openTime
  def getCloseTime: Long = closeTime
  def getOpenPrice: Price = openPrice
  def getClosePrice: Price = closePrice
  def getMinPrice: Price = minPrice
  def getMaxPrice: Price = maxPrice
  def getListAsksStack: util.List[CallOrderStack] = listAsksStack.toList.asJava
  def getListBidsStack: util.List[CallOrderStack] = listBidsStack.toList.asJava
  def getListPerfectOrderStack: util.List[PerfectOrderStack] = listPerfectOrderStack.toList.asJava
  def getListPriceStack: util.List[Price] = listPrice.toList.asJava

  def itemizationPrice(price: Price): (List[PerfectOrderStack], List[CallOrderStack], List[CallOrderStack]) = {
    val startTime = price.getTimestamp-RANGE_ITEMIZATION_MILLIS
    val endTime = price.getTimestamp+RANGE_ITEMIZATION_MILLIS
    val listPerfectOrder = getPerfectOrderRange(startTime, endTime)
    val listAsksStack = getAsksStackRange(startTime, endTime)
    val listBidsStack = getBidsRange(startTime, endTime)
    (listPerfectOrder, listAsksStack, listBidsStack)
  }

  private def getPerfectOrderRange(startTime: Long, endTime: Long): List[PerfectOrderStack] = listPerfectOrderStack.filter(stack=>{
    stack.getStartTime>=startTime &&  stack.getEndTime<=endTime
  }).toList

  private def getBidsRange(startTime: Long, endTime: Long): List[CallOrderStack] = {
    listBidsStack.filter(stack => {
      stack.getTimestamp >= startTime && stack.getTimestamp <= endTime
    }).toList
  }

  private def getAsksStackRange(startTime: Long, endTime: Long): List[CallOrderStack] = {
    listAsksStack.filter(stack => {
      stack.getTimestamp >= startTime && stack.getTimestamp <= endTime
    }).toList
  }

  def isCorrectTimestamp(timestamp: Long): Boolean = {
    timestamp>openTime && timestamp<closeTime
  }

  def addPerfectOrderStack(perfectOrderStack: PerfectOrderStack): Unit = {
    if (perfectOrderStack.getStartTime<openTime || perfectOrderStack.getStartTime>closeTime)
      throw new Exception("Not correct timestamp for perfect order")
    if (!currency.equals(perfectOrderStack.getCurrency))
      throw new Exception("Not correct perfectOrderStack currency "+perfectOrderStack.getCurrency+". Expected "+currency)
    if (!place.equals(perfectOrderStack.getPlace))
      throw new Exception("Not correct perfectOrderStack place "+perfectOrderStack.getPlace+". Expected "+place)
    listPerfectOrderStack.append(perfectOrderStack)
  }

  def addAsksStack(callOrderStack: CallOrderStack): Unit = {
    if (callOrderStack.getTimestamp<openTime || callOrderStack.getTimestamp>closeTime)
      throw new Exception("Not correct timestamp for call order")
    if (!currency.equals(callOrderStack.getCurrency))
      throw new Exception("Not correct callOrderStack currency "+callOrderStack.getCurrency+". Expected "+currency)
    if (!place.equals(callOrderStack.getPlace))
      throw new Exception("Not correct callOrderStack place "+callOrderStack.getPlace+". Expected "+place)
    listAsksStack.append(callOrderStack)
  }

  def addBidsStack(callOrderStack: CallOrderStack): Unit = {
    if (callOrderStack.getTimestamp<openTime || callOrderStack.getTimestamp>closeTime)
      throw new Exception("Not correct timestamp for call order")
    if (!currency.equals(callOrderStack.getCurrency))
      throw new Exception("Not correct callOrderStack currency "+callOrderStack.getCurrency+". Expected "+currency)
    if (!place.equals(callOrderStack.getPlace))
      throw new Exception("Not correct callOrderStack place "+callOrderStack.getPlace+". Expected "+place)
    listBidsStack.append(callOrderStack)
  }

  def addPrice(price: Price): Unit = {
    if (price.timestamp<openTime || price.timestamp>closeTime)
      throw new Exception("Not correct price timestamp")
    if (!currency.equals(price.getCurrency))
      throw new Exception("Not correct price currency "+price.getCurrency+". Expected "+currency)
    if (!place.equals(price.getPlace))
      throw new Exception("Not correct price place "+price.getPlace+". Expected "+place)

    if (openPrice == null) {
      openPrice=price
      closePrice=price
      maxPrice=price
      minPrice=price
      return
    }

    if (getMaxCost<price.cost)
      maxPrice=price
    else if (getMinCost>price.cost)
      minPrice=price

    closePrice=price
    listPrice.append(price)
  }

  private def getMaxCost: Float = if (maxPrice==null) 0f else maxPrice.cost
  private def getMinCost: Float = if (minPrice==null) 0f else minPrice.cost
  private def getOpenCost: Float = if (openPrice==null) 0f else openPrice.cost
  private def getCloseCost: Float = if (closePrice==null) 0f else closePrice.cost

//  def isPositiveCourse: Boolean = getCloseCost>getOpenCost
  def calcDivergence: Float = getCloseCost - getOpenCost
  def calcDivergencePercent: Float = (getCloseCost / (getOpenCost / 100f)) - 100f

  override def toString: String = {
    "{currency: " + currency + "\n" +
      "openTime: " + openTime + "\n" +
      "closeTime: " + closeTime + "\n" +
      "place: " + place + "\n" +
      "openPrice: " + openPrice + "\n" +
      "closePrice: " + closePrice + "\n" +
      "minPrice: " + minPrice + "\n" +
      "maxPrice: " + maxPrice + "\n" + (if (!listPrice.isEmpty) listPrice.map(_.toString).reduce((x1,x2)=>{x1+x2})+"} \n" else "")
    //      listAsksStack.map(_.toString).reduce((x1,x2)=>{x1+x2}) +
    //      listBidsStack.map(_.toString).reduce((x1,x2)=>{x1+x2}) +
    //      listPerfectOrderStack.map(_.toString).reduce((x1,x2)=>{x1+x2})
  }

//  def getAgitatedLevel: AgitatedLevel = AgitatedSystem.identify(getDivergencePercent)
//    def isRelevantDivergence: Boolean = {
//      getDivergence >= getBuySellTaxes
//    }
//  //
//    def getBuySellTaxes: Float = {
//      val buyTaxes = lastPrice * Constants.Binance.ORDER_TAXES
//      val sellTaxes = (lastPrice+buyTaxes) * Constants.Binance.ORDER_TAXES
//      buyTaxes+sellTaxes
//    }
  //
}
