package com.example.rateswap.domain.usecases

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case to calculate the commission fee based on the transaction amount and count.
 * If the number of transactions exceeds a predefined limit, a commission fee is applied.
 *
 * @property amount The transaction amount.
 * @property transactionCount The number of transactions made.
 * @return The calculated commission fee, or 0.00 if within the free transaction limit.
 */

@Singleton
class CalculateCommissionFee @Inject constructor() {

   operator fun invoke(amount: Double, transactionCount: Int): Double {
        return if (transactionCount >= FREE_TRANSACTIONS_LIMIT) {
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