package ru.broom.telegram_notifier.service.`trait`

import java.sql.SQLException
import java.util

trait NotificationDerbyTrait extends DerbyConnection {

  private def createSubscribersTableIfNotExist(): Unit = {
    val res = connection.getMetaData.getTables(null, "APP", "SUBSCRIBERS", null)
    if (!res.next) {
      val statement = connection.prepareStatement("CREATE TABLE SUBSCRIBERS(chat_id BIGINT NOT NULL)")
      statement.execute
    }
  }

  def saveSubscriber(chatId: Long): Unit = {
    try if (!getSubscribers.contains(chatId)) {
      createConnectionIfClosed()
      createSubscribersTableIfNotExist()
      val statement = connection.prepareStatement("INSERT INTO SUBSCRIBERS VALUES (" + chatId + ")")
      statement.execute
    }
    catch {
      case e: SQLException =>
        e.printStackTrace()
    } finally closeAll()
  }

  def getSubscribers: util.List[Long] = {
    val chatIds = new util.ArrayList[Long]
    try {
      createConnectionIfClosed()
      createSubscribersTableIfNotExist()
      val statement = connection.prepareStatement("SELECT DISTINCT(chat_id) FROM SUBSCRIBERS")
      val resultSet = statement.executeQuery
      while ( {
        resultSet.next
      }) chatIds.add(resultSet.getLong("chat_id"))
    } catch {
      case e: SQLException =>
        e.printStackTrace()
    } finally closeAll()
    chatIds
  }
}
