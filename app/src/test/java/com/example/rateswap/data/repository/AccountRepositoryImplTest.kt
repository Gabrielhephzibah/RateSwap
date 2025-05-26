package com.example.rateswap.data.repository

import com.example.rateswap.data.local.dao.AccountDao
import com.example.rateswap.domain.repository.AccountRepository
import com.example.rateswap.util.TestData
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AccountRepositoryImplTest {
    private lateinit var accountRepository: AccountRepository
    private val mockAccountDao = mockk<AccountDao>()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        accountRepository = AccountRepositoryImpl(mockAccountDao, testDispatcher)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getAccountBalances returns list of account balance`() = runTest {
        val expected = TestData.accountBalanceList
        coEvery { mockAccountDao.getAllAccounts() } returns flowOf(TestData.accountEntityList)
        val actual = accountRepository.getAccountBalances().first()

        assertThat(actual).isEqualTo(expected)

        coVerify(exactly = 1) { mockAccountDao.getAllAccounts() }

    }

    @Test
    fun `insertAccountBalance inserts account entity into database`() = runTest {
        val accountEntity = TestData.accountEntityUSD
        val accountBalance = TestData.accountBalanceUSD

        coJustRun { mockAccountDao.insertAccount(accountEntity) }

        accountRepository.insertAccountBalance(accountBalance)

        coVerify { mockAccountDao.insertAccount(accountEntity) }
    }

    @Test
    fun `updateAccountBalance updates account entity in database`() = runTest {
        val accountEntity = TestData.accountEntityUSD
        val accountBalance = TestData.accountBalanceUSD

        coJustRun { mockAccountDao.updateAccount(accountEntity) }

        accountRepository.updateAccountBalance(accountBalance)

        coVerify { mockAccountDao.updateAccount(accountEntity) }
    }

}
