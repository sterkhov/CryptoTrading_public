package ru.broom.mongo_storage.config

import java.util.Properties

object MongoStorageProperties {
  private val properties = new Properties()
  properties.load(getClass.getResource("/mongo-storage.properties").openStream())
  object Connection {
    val URL = properties.getProperty("mongo.connection.url")
  }
}
