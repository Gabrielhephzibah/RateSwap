package com.example.rateswap.data.local.datastore

import kotlinx.coroutines.flow.Flow

interface TransactionDataStore {
    fun getTransactionCount(): Flow<Int>
    suspend fun setTransactionCount(count: Int)
}