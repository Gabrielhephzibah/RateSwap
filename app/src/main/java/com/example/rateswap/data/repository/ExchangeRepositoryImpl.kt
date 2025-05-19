package com.example.rateswap.data.repository

import com.example.rateswap.data.remote.ExchangeApi
import com.example.rateswap.domain.repository.ExchangeRepository
import com.example.rateswap.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExchangeRepositoryImpl @Inject constructor(
    private val exchangeApi: ExchangeApi
): ExchangeRepository {
    override fun getExchangeRates(): Flow<Resource<Map<String, Double>>> =
        flow<Resource<Map<String, Double>>> {
            val response = exchangeApi.getExchangeRates()
            emit(Resource.Success(response.rates))
        }.flowOn(Dispatchers.IO).catch {
            emit(Resource.Error(it))
        }
}