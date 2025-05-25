package com.example.rateswap.domain.repository

import com.example.rateswap.domain.model.AccountBalance
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAccountBalances(): Flow<List<AccountBalance>>
    suspend fun insertAccountBalance(accountBalance: AccountBalance)
    suspend fun updateAccountBalance(accountBalance: AccountBalance)
}