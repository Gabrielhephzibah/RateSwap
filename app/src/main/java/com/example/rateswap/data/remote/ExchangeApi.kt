package com.example.rateswap.data.remote

import com.example.rateswap.data.remote.dto.ExchangeDto
import retrofit2.http.GET

interface ExchangeApi {
    @GET("currency-exchange-rates")
    suspend fun getExchangeRates(): ExchangeDto
}