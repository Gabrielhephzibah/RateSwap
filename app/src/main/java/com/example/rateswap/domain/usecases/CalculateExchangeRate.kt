package com.example.rateswap.domain.usecases

import com.example.rateswap.data.mappers.toTwoDecimal
import com.example.rateswap.domain.model.ExchangeRate
import com.example.rateswap.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalculateExchangeRate @Inject constructor(){

    operator fun invoke(
        amount: String,
        sellingCurrency: String,
        buyingCurrency: String,
        exchangeRate: ExchangeRate,
    ): Flow<Resource<Double>> =
        flow {
            val amountToDouble = amount.toDoubleOrNull() ?: 0.0
            val fromRate = exchangeRate.rates[sellingCurrency] ?: 0.0
            val toRate = exchangeRate.rates[buyingCurrency] ?: 0.0

            val convert = when {
                fromRate == 0.0 && toRate == 0.0 -> 0.0
                else -> toRate / fromRate
            }
            val totalAmount = amountToDouble * convert
            emit(Resource.Success(totalAmount.toTwoDecimal()))
        }
}