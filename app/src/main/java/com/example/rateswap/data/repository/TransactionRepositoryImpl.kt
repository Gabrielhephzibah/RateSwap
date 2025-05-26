package com.example.rateswap.data.repository

import com.example.rateswap.data.local.datastore.TransactionDataStore
import com.example.rateswap.domain.repository.TransactionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDataStore: TransactionDataStore,
    private val ioDispatcher: CoroutineDispatcher
) : TransactionRepository {

    override fun getTransactionCount(): Flow<Int> {
        return transactionDataStore.getTransactionCount().flowOn(ioDispatcher)
    }

    override suspend fun setTransactionCount(count: Int) {
        transactionDataStore.setTransactionCount(count)
    }
}
