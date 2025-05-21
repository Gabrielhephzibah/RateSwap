package com.example.rateswap.domain.model

data class ExchangeRate(
    val rates: Map<String, Double> = emptyMap()
)