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
        receivingAmount: Double,
        accountBalance: List<AccountBalance>,
    ): Resource<Unit> {
        val existingAccount = accountBalance.firstOrNull { it.currency == buyingCurrency }

        existingAccount?.let { account ->
            val totalAmount = account.balance + receivingAmount
            val updateAccount = existingAccount.copy(balance = totalAmount)
            println("ZIBAH:: AccountExist :: $updateAccount")
            accountRepository.updateAccountBalance(updateAccount)
        } ?: accountRepository.insertAccountBalance(
                AccountBalance(
                    currency = buyingCurrency,
                    balance = receivingAmount
                )
            )
      return Resource.Success(Unit)
    }

}