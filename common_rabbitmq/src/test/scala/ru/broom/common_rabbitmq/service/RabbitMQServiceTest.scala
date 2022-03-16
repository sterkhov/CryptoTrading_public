package ru.broom.common_rabbitmq.service

import com.rabbitmq.client.{CancelCallback, DeliverCallback, Delivery}
import org.junit.runner.RunWith
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.junit.JUnitRunner
import ru.broom.common_rabbitmq.service.`trait`.RabbitMQConnection

@RunWith(classOf[JUnitRunner])
class RabbitMQServiceTest extends AnyFunSuite {

  test("rabbitmq test") {
    val c = new RabbitMQConnection(){

    }

    c.chanel.queueDeclare("TEST", false, false, false, null);
    val message = "Hello World!";
    c.chanel.basicPublish("", "TEST", null, message.getBytes());
    System.out.println(" [x] Sent '" + message + "'");


    val deliverCallback: DeliverCallback = (consumerTag: String, delivery: Delivery) => {
      def foo(consumerTag: String, delivery: Delivery) = {
        val message = new String(delivery.getBody, "UTF-8")
        System.out.println(" [x] Received '" + message + "'")
      }

      foo(consumerTag, delivery)
    }

    val cancelCallback: CancelCallback = (consumerTag: String) => {

    }

    c.chanel.basicConsume("TEST", deliverCallback, cancelCallback)

  }



}
