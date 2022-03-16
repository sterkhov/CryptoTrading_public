package ru.broom.mongo_storage.service.`trait`

import com.mongodb.{ConnectionString, MongoClientSettings}
import com.mongodb.client.MongoClients
import ru.broom.mongo_storage.config.MongoStorageProperties

trait MongoConnection {
  protected val mongoClient = MongoClients.create(
    MongoClientSettings.builder.applyConnectionString(new ConnectionString(MongoStorageProperties.Connection.URL)).build
  )
}
