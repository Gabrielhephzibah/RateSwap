package com.example.rateswap.presentation

import com.example.rateswap.domain.model.AccountBalance
import com.example.rateswap.domain.model.ExchangeRate
import com.example.rateswap.domain.usecases.CalculateCommissionFee
import java.util.Currency

data class MainScreenState(
    val exchangeRates: ExchangeRate = ExchangeRate(),
    val amountToReceive : Double = 0.0,
    val accountBalances: List<AccountBalance> = emptyList(),
    val commissionFee: Double = 0.0,
    val totalAmountDeducted : Double = 0.0,
    val exchangeRateError : String = "",
 )