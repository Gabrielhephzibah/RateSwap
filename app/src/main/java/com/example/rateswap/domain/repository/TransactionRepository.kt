package com.example.rateswap.domain.repository

import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getTransactionCount(): Flow<Int>
    suspend fun setTransactionCount(count: Int)
}