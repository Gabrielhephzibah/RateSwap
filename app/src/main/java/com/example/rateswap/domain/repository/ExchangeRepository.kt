package com.example.rateswap.domain.repository

import com.example.rateswap.domain.model.ExchangeRate
import com.example.rateswap.utils.Resource
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface ExchangeRepository {
    fun getExchangeRates(): Flow<Resource<ExchangeRate>>



//    suspend fun getExchangeRate(baseCurrency: String, targetCurrency: String): Double
//    suspend fun convertCurrency(amount: Double, baseCurrency: String, targetCurrency: String): Double
}