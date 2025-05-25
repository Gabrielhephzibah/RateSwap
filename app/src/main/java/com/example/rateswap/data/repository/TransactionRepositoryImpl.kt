package com.example.rateswap.data.repository

import com.example.rateswap.data.local.datastore.TransactionDataStore
import com.example.rateswap.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDataStore: TransactionDataStore
) : TransactionRepository {

    override fun getTransactionCount(): Flow<Int> {
        return transactionDataStore.getTransactionCount()
    }

    override suspend fun setTransactionCount(count: Int) {
        transactionDataStore.setTransactionCount(count)
    }
}
