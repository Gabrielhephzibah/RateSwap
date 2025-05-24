package com.example.rateswap.domain.repository

import com.example.rateswap.domain.model.AccountBalance
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAccountBalance(): Flow<List<AccountBalance>>
    suspend fun insertAccountBalance(accountBalance: AccountBalance)
    suspend fun updateAccountBalance(accountBalance: AccountBalance)
    fun getTransactionCount(): Flow<Int>
    suspend fun setTransactionCount(count: Int)
}