package ru.broom.telegram_notifier.service

import java.sql.SQLException

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.request.{GetUpdates, SendMessage}
import ru.broom.telegram_notifier.service.`trait`.{NotificationDerbyTrait, TelegramConnection}

object TelegramNotifier extends NotificationDerbyTrait with TelegramConnection {
  def notifyException(e: Exception): Unit = {
    var message = e.getMessage
    if (!e.getStackTrace.isEmpty)
      message = message + "\n" + e.getStackTrace.map(_.toString).reduce((x1,x2)=>x1+"\n"+x2)
    notifyMessage(message)
  }
  def notifyMessage(message: String): Unit = {
    try {
      saveNewSubscribers(telegramBot)
      getSubscribers.forEach((chatId) => {
        telegramBot.execute(new SendMessage(chatId, message))
      })
    } catch {
      case e: SQLException =>
        e.printStackTrace()
    }
  }
  private def saveNewSubscribers(telegramBot: TelegramBot): Unit = {
    val getUpdates = new GetUpdates().limit(100).offset(0).timeout(0)
    val updatesResponse = telegramBot.execute(getUpdates)
    val updates = updatesResponse.updates
    updates.forEach(update => {
      if (update.message.text == "/start") saveSubscriber(update.message.chat.id)
    })
  }
}
