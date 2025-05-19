package com.example.rateswap.domain.repository

import com.example.rateswap.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ExchangeRepository {
    fun getExchangeRates(): Flow<Resource<Map<String, Double>>>
//    suspend fun getExchangeRate(baseCurrency: String, targetCurrency: String): Double
//    suspend fun convertCurrency(amount: Double, baseCurrency: String, targetCurrency: String): Double
}