package ru.broom.telegram_notifier.config

import java.util.Properties

object TelegramNotifierProperties {
  private val properties = new Properties()
  properties.load(getClass.getResource("/telegram-notifier.properties").openStream())
  object TelegramProperties {
    var telegramToken = properties.getProperty("telegram.token")
  }
}
