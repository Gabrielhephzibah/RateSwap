package com.example.rateswap.domain.usecases

import com.example.rateswap.domain.model.AccountBalance
import com.example.rateswap.utils.ErrorMessage
import com.example.rateswap.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case to validate the exchange amount before proceeding with the transaction.
 *
 * @property commissionFee The use case to calculate the commission fee based on the transaction count.
 */
@Singleton
class ValidateExchangeAmount @Inject constructor(
    private val commissionFee: CalculateCommissionFee
) {

    operator fun invoke(
        sellingAmount: String,
        sellingCurrency: String,
        buyingCurrency: String,
        accountBalances: List<AccountBalance>,
        transactionCount :Int
    ): Flow<Resource<String>> =
        flow {
            val sellingCurrencyBalance = accountBalances.find { it.currency == sellingCurrency }
            val amountToDouble = sellingAmount.toDoubleOrNull() ?: 0.0
            when {
                sellingCurrencyBalance == null -> {
                    emit(Resource.Error("${ErrorMessage.NO_ACCOUNT_FOUND} $sellingCurrency."))
                }
                sellingAmount == "0" -> {
                    emit(Resource.Error(ErrorMessage.AMOUNT_GRATER_THAN_ZERO))
                }

                sellingCurrencyBalance.balance < (amountToDouble + commissionFee(amountToDouble, transactionCount)) -> {
                    emit(Resource.Error(ErrorMessage.NOT_ENOUGH_BALANCE))
                }

                sellingCurrency == buyingCurrency -> {
                    emit(Resource.Error(ErrorMessage.SAME_CURRENCY))
                }

                else -> {
                    emit(Resource.Success(sellingAmount))
                }
            }
        }
}