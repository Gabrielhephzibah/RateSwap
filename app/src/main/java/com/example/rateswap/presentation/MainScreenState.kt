package com.example.rateswap.presentation

import com.example.rateswap.domain.model.ExchangeRate
import java.math.BigDecimal

data class MainScreenState(
     val exchangeRates: ExchangeRate = ExchangeRate(),
     val amountToReceive : Double = 0.0,
 )