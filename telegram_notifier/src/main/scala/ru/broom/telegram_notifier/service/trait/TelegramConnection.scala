package ru.broom.telegram_notifier.service.`trait`

import java.net.{InetSocketAddress, Proxy}
import java.util.concurrent.TimeUnit

import com.pengrad.telegrambot.TelegramBot
import okhttp3.OkHttpClient
import ru.broom.telegram_notifier.config.TelegramNotifierProperties

trait TelegramConnection {
  val telegramBot: TelegramBot = new TelegramBot.Builder(TelegramNotifierProperties.TelegramProperties.telegramToken)
                                                .okHttpClient(getClient).build

  private def getClient = new OkHttpClient.Builder()
                          .connectTimeout(60, TimeUnit.SECONDS)
                          .writeTimeout(60, TimeUnit.SECONDS)
                          .readTimeout(60, TimeUnit.SECONDS)
                          .build

  private def getProxyClient(proxyHost: String, proxyPort: Int) = new OkHttpClient.Builder()
                                                                    .connectTimeout(60, TimeUnit.SECONDS)
                                                                    .writeTimeout(60, TimeUnit.SECONDS)
                                                                    .readTimeout(60, TimeUnit.SECONDS)
                                                                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)))
                                                                    .build
}
