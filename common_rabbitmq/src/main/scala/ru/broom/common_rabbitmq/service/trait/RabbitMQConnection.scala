package ru.broom.common_rabbitmq.service.`trait`

import com.rabbitmq.client.ConnectionFactory
import ru.broom.common_rabbitmq.config.CommonRabbitMQProperties

trait RabbitMQConnection {
  private val connectionFactory = new ConnectionFactory()
  connectionFactory.setHost(CommonRabbitMQProperties.RabbitMQ.host);
  private val connection = connectionFactory.newConnection()
  val chanel = connection.createChannel()
}
