package com.example.rateswap.domain.usecases

import com.example.rateswap.domain.model.AccountBalance
import com.example.rateswap.domain.repository.AccountRepository
import com.example.rateswap.utils.Resource
import javax.inject.Inject

class CalculateSellingAccountBalance @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    suspend  operator fun invoke(
        sellingCurrency: String,
        amountToSell : String,
        accountBalance: List<AccountBalance>
    ): Resource<Unit> {
        val amountToDouble = amountToSell.toDoubleOrNull() ?: 0.0
        val existingAccount = accountBalance.find { it.currency == sellingCurrency }

        existingAccount?.let { account ->
            val totalAmount = account.balance - amountToDouble
            val updateAccount = existingAccount.copy(balance = totalAmount)
            println("ZIBAH:: AccountExist :: $updateAccount")
            accountRepository.updateAccountBalance(updateAccount)
        }
        return Resource.Success(Unit)
    }
}