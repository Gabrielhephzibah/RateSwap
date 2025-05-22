package com.example.rateswap.presentation

import com.example.rateswap.domain.model.AccountBalance
import com.example.rateswap.domain.model.ExchangeRate

data class MainScreenState(
    val exchangeRates: ExchangeRate = ExchangeRate(),
    val amountToReceive : Double = 0.0,
    val accountBalances: List<AccountBalance> = emptyList(),
    val exchangeValidationError: String = "",
 )