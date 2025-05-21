package com.example.rateswap.domain.repository

import com.example.rateswap.domain.model.Account
import com.example.rateswap.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAccountBalance(): Flow<List<Account>>
    fun insertAccountBalance(balance: Double)
}