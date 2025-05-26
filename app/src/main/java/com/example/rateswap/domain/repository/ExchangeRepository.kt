package com.example.rateswap.domain.repository

import com.example.rateswap.domain.model.ExchangeRate
import com.example.rateswap.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ExchangeRepository {
    fun getExchangeRates(): Flow<Resource<ExchangeRate>>
}