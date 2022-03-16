package ru.broom.telegram_notifier.service

import org.junit.runner.RunWith
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class NotificationServiceTest extends AnyFunSuite {
  test("Test notify") {
      TelegramNotifier.notifyMessage("TEST")
  }
}
