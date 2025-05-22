package com.example.rateswap.domain.repository

import com.example.rateswap.domain.model.AccountBalance
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAccountBalance(): Flow<List<AccountBalance>>
    fun insertAccountBalance(balance: Double)
}