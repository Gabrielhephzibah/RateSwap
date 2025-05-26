package com.example.rateswap.data.repository

import com.example.rateswap.data.mappers.toExchangeRate
import com.example.rateswap.data.remote.ExchangeApi
import com.example.rateswap.domain.model.ExchangeRate
import com.example.rateswap.domain.repository.ExchangeRepository
import com.example.rateswap.utils.ErrorMessage
import com.example.rateswap.utils.Resource
import com.example.rateswap.utils.connectivity.ConnectivityObserver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExchangeRepositoryImpl @Inject constructor(
    private val exchangeApi: ExchangeApi,
    private val connectivityObserver: ConnectivityObserver,
    private val ioDispatcher: CoroutineDispatcher
): ExchangeRepository {
    override fun getExchangeRates(): Flow<Resource<ExchangeRate>> =
        flow {
            while(currentCoroutineContext().isActive) {
                if (!connectivityObserver.isConnected) {
                    emit(Resource.Error(ErrorMessage.NO_INTERNET_CONNECTION))
                    delay(DELAY_TIME)
                    continue
                }
                val response = exchangeApi.getExchangeRates()
                emit(Resource.Success(response.toExchangeRate()))
                delay(DELAY_TIME)

            }
        }.flowOn(ioDispatcher)
            .catch {
            emit(Resource.Error(ErrorMessage.API_ERROR))
        }

    companion object{
       const val DELAY_TIME = 5000L
    }
}