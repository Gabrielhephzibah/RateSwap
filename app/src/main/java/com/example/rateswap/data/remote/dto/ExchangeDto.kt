package com.example.rateswap.data.remote.dto

data class ExchangeDto(
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)
