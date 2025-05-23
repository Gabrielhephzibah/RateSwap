package com.example.rateswap.data.mappers

import com.example.rateswap.data.remote.dto.ExchangeDto
import com.example.rateswap.domain.model.ExchangeRate

fun ExchangeDto.toExchangeRate(): ExchangeRate {
    return ExchangeRate(
        rates.map { (key, value) ->
            key.uppercase() to value.toTwoDecimal()
        }.toMap()
    )
}