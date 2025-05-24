package com.example.rateswap.domain.usecases

import com.example.rateswap.domain.datastore.TransactionDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalculateCommissionFee @Inject constructor(
) {

   operator fun invoke(amount: Double, transactionCount: Int): Double {
        return if (transactionCount > FREE_TRANSACTIONS_LIMIT) {
            COMMISSION_RATE * amount
        } else {
            0.00
        }
    }

    private companion object {
        const val FREE_TRANSACTIONS_LIMIT = 5
        const val COMMISSION_RATE = 0.007
    }
}