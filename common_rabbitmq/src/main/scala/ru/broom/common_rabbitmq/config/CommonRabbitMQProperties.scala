package ru.broom.common_rabbitmq.config

import java.util.Properties

object CommonRabbitMQProperties {
  private val properties = new Properties();
  properties.load(getClass.getResource("/common-rabbitmq.properties").openStream())
  object RabbitMQ {
    val host: String = properties.getProperty("rabbitmq.host")
  }
}
