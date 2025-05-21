package com.example.rateswap.data.mappers

import com.example.rateswap.data.remote.dto.ExchangeDto
import com.example.rateswap.domain.model.ExchangeRate

fun ExchangeDto.toExchangeRate(): ExchangeRate {
    return ExchangeRate(
        rates = rates.mapValues { it.value.toTwoDecimal() }
    )
}