package com.example.rateswap.domain.usecases

import com.example.rateswap.domain.model.AccountBalance
import com.example.rateswap.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidateExchangeAmount @Inject constructor(
    private val commissionFee: CalculateCommissionFee
) {

    operator fun invoke(
        amount: String,
        sellingCurrency: String,
        buyingCurrency: String,
        accountBalance: List<AccountBalance>,
        transactionCount :Int
    ): Flow<Resource<String>> =
        flow {
            val sellingCurrencyBalance = accountBalance.find { it.currency == sellingCurrency }
            println("ZIBAH:: sellingCurrencyBalance: $sellingCurrencyBalance")
            println("ZIBAH:: sellingCurrency: $sellingCurrency")
            val amountToDouble = amount.toDoubleOrNull() ?: 0.0
            when {
                sellingCurrencyBalance == null -> {
                    emit(Resource.Error(Throwable("You do not have this currency in your account")))
                }
                amount == "0" -> {
                    emit(Resource.Error(Throwable("Enter an amount greater than 0")))
                }

                sellingCurrencyBalance.balance < (amountToDouble + commissionFee(amountToDouble, transactionCount)) -> {
                    emit(Resource.Error(Throwable("You do not have enough balance to exchange this amount")))
                }

                sellingCurrency == buyingCurrency -> {
                    emit(Resource.Error(Throwable("You cannot exchange the same currency")))
                }

                else -> {
                    emit(Resource.Success(amount))
                }
            }
        }
}