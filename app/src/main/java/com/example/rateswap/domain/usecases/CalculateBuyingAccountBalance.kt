package com.example.rateswap.domain.usecases

import com.example.rateswap.domain.model.AccountBalance
import com.example.rateswap.domain.repository.AccountRepository
import com.example.rateswap.utils.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalculateBuyingAccountBalance @Inject constructor(
    private val accountRepository: AccountRepository,
) {

    suspend operator fun invoke(
        buyingCurrency: String,
        buyingAmount: Double,
        accountBalances: List<AccountBalance>,
    ): Resource<Unit> {
        val existingAccount = accountBalances.firstOrNull { it.currency == buyingCurrency }

        existingAccount?.let { account ->
            val totalAmount = account.balance + buyingAmount
            val updateAccount = existingAccount.copy(balance = totalAmount)
            accountRepository.updateAccountBalance(updateAccount)
        } ?: accountRepository.insertAccountBalance(
                AccountBalance(
                    currency = buyingCurrency,
                    balance = buyingAmount
                )
            )
      return Resource.Success(Unit)
    }

}