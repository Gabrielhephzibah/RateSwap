package com.example.rateswap.data.repository

import com.example.rateswap.data.mappers.toExchangeRate
import com.example.rateswap.data.remote.ExchangeApi
import com.example.rateswap.domain.model.ExchangeRate
import com.example.rateswap.domain.repository.ExchangeRepository
import com.example.rateswap.utils.Resource
import com.example.rateswap.utils.connectivity.ConnectivityObserver
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
    private val connectivityObserver: ConnectivityObserver
): ExchangeRepository {
    override fun getExchangeRates(): Flow<Resource<ExchangeRate>> =
        flow<Resource<ExchangeRate>> {
            println("ZIBAH:: ISACTIVE ${currentCoroutineContext().isActive}")
            println("ZIBAH:: Connectivity ${connectivityObserver.isConnected}")
            while(currentCoroutineContext().isActive) {
                if (!connectivityObserver.isConnected) {
                    emit(Resource.Error(Exception("Couldn't reach server. Check your internet connection and try again.")))
                    println("ZIBAH:: No Internet Connection")
                    delay(5000)
                    continue
                }

                val response = exchangeApi.getExchangeRates()
                emit(Resource.Success(response.toExchangeRate()))
                delay(5000)

//                if (!connectivityObserver.isConnected) {
//                    emit(Resource.Error(Exception("No internet connection")))
//                    println("ZIBAH:: No Internet Connection")
//                }else{
//                    val response = exchangeApi.getExchangeRates()
//                    emit(Resource.Success(response.toExchangeRate()))
//                }
//                delay(5000)
            }
        }.flowOn(Dispatchers.IO).catch {
            emit(Resource.Error(Exception("Error fetching exchange rates, please try again later.")))
            println("ZIBAH:: Error Repository: $it")
        }
}