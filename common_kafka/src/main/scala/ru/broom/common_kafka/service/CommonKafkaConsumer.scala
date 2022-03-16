package ru.broom.common_kafka.service

import java.util
import java.util.Properties

import com.fasterxml.jackson.databind.JsonNode
import org.apache.kafka.clients.consumer.KafkaConsumer

trait CommonKafkaConsumer {
  private val properties = new Properties();
  properties.load(getClass.getResource("/common-kafka-consumer.properties").openStream())
  protected def startSubscribe(topics: util.Set[String]): KafkaConsumer[String, JsonNode] = {
    val consumer = new KafkaConsumer[String, JsonNode](properties)
    consumer.subscribe(topics);
    consumer
  }
}
