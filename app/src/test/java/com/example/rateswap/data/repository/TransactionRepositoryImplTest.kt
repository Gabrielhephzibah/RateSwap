package com.example.rateswap.data.repository

import com.example.rateswap.data.local.datastore.TransactionDataStore
import com.example.rateswap.domain.repository.TransactionRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionRepositoryImplTest {
    private lateinit var transactionRepository: TransactionRepository
    private val mockTransactionDao = mockk<TransactionDataStore>()
    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        transactionRepository = TransactionRepositoryImpl(mockTransactionDao, dispatcher)
    }

    @Test
    fun `getTransactionCount returns transaction count`() = runTest {
        coEvery { transactionRepository.getTransactionCount() } returns flowOf(5)

        val actual = transactionRepository.getTransactionCount().first()
        assertThat(actual).isEqualTo(5)
        coVerify { mockTransactionDao.getTransactionCount() }
    }

    @Test
    fun `setTransactionCount sets transaction count`() = runTest {
        val count = 10
        coJustRun { mockTransactionDao.setTransactionCount(count) }

        transactionRepository.setTransactionCount(count)

        coVerify { mockTransactionDao.setTransactionCount(count) }
    }
}