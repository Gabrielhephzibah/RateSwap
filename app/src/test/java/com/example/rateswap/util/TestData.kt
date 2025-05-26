package com.example.rateswap.util

import com.example.rateswap.data.local.entity.AccountEntity
import com.example.rateswap.data.remote.dto.ExchangeDto
import com.example.rateswap.domain.model.AccountBalance
import com.example.rateswap.domain.model.ExchangeRate

object TestData {
    const val AMOUNT = 100.0
    const val COMMISSION_PERCENTAGE = 0.007
    const val BUYING_CURRENCY = "USD"
    const val SELLING_CURRENCY = "EUR"
    val exchangeRatesDto = ExchangeDto (
        base = "EUR",
        date = "2025-05-20",
        rates =  mapOf(
        "EUR" to 1.0,
        "USD" to 1.12,
    ))
    val exchangeRate = ExchangeRate(
        rates = mapOf(
            "EUR" to 1.0,
            "USD" to 1.12,
        )
    )

    val accountBalanceUSD = AccountBalance(id = 1,currency = "USD", balance = 100.0)
    val accountBalanceEUR = AccountBalance(id = 2, currency = "EUR", balance = 100.0)
    val accountEntityUSD = AccountEntity(id = 1,currency = "USD", balance = 100.0)
    val accountEntityEUR = AccountEntity(id = 2, currency = "EUR", balance = 100.0)
    val accountEntityList = listOf(accountEntityEUR, accountEntityUSD)
    val accountBalanceList = listOf(accountBalanceEUR, accountBalanceUSD)
}