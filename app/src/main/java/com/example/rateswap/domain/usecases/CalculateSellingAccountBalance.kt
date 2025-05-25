package com.example.rateswap.domain.usecases

import com.example.rateswap.domain.model.AccountBalance
import com.example.rateswap.domain.repository.AccountRepository
import com.example.rateswap.utils.Resource
import javax.inject.Inject

class CalculateSellingAccountBalance @Inject constructor(
    private val accountRepository: AccountRepository,
    private val commissionFee: CalculateCommissionFee
) {
    suspend  operator fun invoke(
        sellingCurrency: String,
        sellingAmount : String,
        accountBalances: List<AccountBalance>,
        transactionCount : Int
    ): Resource<Unit> {
        val amountToDouble = sellingAmount.toDoubleOrNull() ?: 0.0
        val existingAccount = accountBalances.find { it.currency == sellingCurrency }

        existingAccount?.let { account ->
            val totalAmount = account.balance - (amountToDouble + commissionFee(amountToDouble, transactionCount))
            val updateAccount = existingAccount.copy(balance = totalAmount)
            accountRepository.updateAccountBalance(updateAccount)
        }
        return Resource.Success(Unit)
    }
}