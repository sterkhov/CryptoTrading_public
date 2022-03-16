package ru.broom.mongo_storage.service

import com.mongodb.client.MongoCursor
import org.bson.codecs.ValueCodecProvider
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.jsr310.Jsr310CodecProvider
import org.bson.codecs.pojo.PojoCodecProvider
import org.bson.types.ObjectId
import ru.broom.common_trade.model._
import ru.broom.mongo_storage.service.`trait`.MongoConnection
import com.mongodb.client.model.Filters._

import scala.collection.mutable.ListBuffer

object CandleStorage extends MongoConnection {

  private def getPojoDatabase(dbName: String) =
    mongoClient.getDatabase(dbName)
      .withCodecRegistry(CodecRegistries
        .fromProviders(
          PojoCodecProvider.builder.register(
            classOf[Candle],
            classOf[CallOrderStack],
            classOf[Price],
            classOf[PerfectOrderStack],
            classOf[CallOrder],
            classOf[PerfectOrder]).build,
          new Jsr310CodecProvider, new ValueCodecProvider))

  def saveCandle(dbName: String, collectionName: String, candle: Candle): Unit = {
    val collection = getPojoDatabase(dbName).getCollection(collectionName, classOf[Candle])
    collection.insertOne(candle)
  }

  def findFirstCandle(dbName: String, collectionName: String): Candle = {
    val collection = getPojoDatabase(dbName).getCollection(collectionName, classOf[Candle])
    collection.find.first
  }

  def findNextCandles(dbName: String, collectionName: String, startObjectId: ObjectId, pageSize: Int): List[Candle] = {
    val collection = getPojoDatabase(dbName).getCollection(collectionName, classOf[Candle])
    val listBuffer = new ListBuffer[Candle]

    var cursor: MongoCursor[Candle] = null
    if (startObjectId == null) {
      val sizeWithoutFirst = pageSize - 1
      val firstCandle = findFirstCandle(dbName, collectionName)
      listBuffer.append(firstCandle)
      cursor = collection.find(gt("_id", firstCandle.getObjectId)).limit(sizeWithoutFirst).iterator()
    } else {
      cursor = collection.find(gt("_id", startObjectId)).limit(pageSize).iterator()
    }

    try {
      while (cursor.hasNext) {
        val candle = cursor.next
        listBuffer.append(candle)
      }
    } finally {
      cursor.close()
    }

    listBuffer.toList
  }
}