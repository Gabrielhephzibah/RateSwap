package com.example.rateswap.domain.datastore

import kotlinx.coroutines.flow.Flow

interface TransactionDataStore {
    fun getTransactionCount(): Flow<Int>
    suspend fun setTransactionCount(count: Int)
}