package ru.broom.currencies_listing_catcher

import ru.broom.currencies_listing_catcher.service.CurrenciesListingCatcherService

object CurrenciesListingCatcher {
  def main(args: Array[String]): Unit = {
    val currenciesListingCatcherService = new CurrenciesListingCatcherService
    while(true){
      currenciesListingCatcherService.checkNewCurrency()
      Thread.sleep(1000)
    }
  }
}
