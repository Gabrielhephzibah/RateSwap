package com.example.rateswap.domain.usecases

import com.example.rateswap.domain.model.AccountBalance
import com.example.rateswap.domain.repository.AccountRepository
import com.example.rateswap.util.TestData
import com.example.rateswap.utils.Resource
import com.google.common.truth.Truth.assertThat
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CalculateBuyingAccountBalanceTest {
    private lateinit var calculateBuyingAccountBalance: CalculateBuyingAccountBalance
    private var mockAccountRepository = mockk<AccountRepository>()

    @Before
    fun setUp() {
        calculateBuyingAccountBalance = CalculateBuyingAccountBalance(mockAccountRepository)
    }

    @Test
    fun `invoke updates existing account balance`() = runTest {

        val existingAccount = TestData.accountBalanceUSD
        val buyingAmount = 50.0
        val updatedAccount = existingAccount.copy(balance = 150.0)

        val balances = listOf(existingAccount)

        coJustRun { mockAccountRepository.updateAccountBalance(any()) }


        val result = calculateBuyingAccountBalance("USD", buyingAmount, balances)

        coVerify { mockAccountRepository.updateAccountBalance(updatedAccount) }
        coVerify(exactly = 0) { mockAccountRepository.insertAccountBalance(any()) }
        assertThat(result).isEqualTo(Resource.Success(Unit))
    }

    @Test
    fun `invoke inserts new account balance when account doesn't exist`() = runTest {
        val balances = listOf(TestData.accountBalanceUSD)
        val buyingAmount = 75.0

        coJustRun { mockAccountRepository.insertAccountBalance(any()) }

        val result = calculateBuyingAccountBalance("GBP", buyingAmount, balances)

        coVerify {
            mockAccountRepository.insertAccountBalance(
                AccountBalance(
                    currency = "GBP",
                    balance = 75.0
                )
            )
        }
        coVerify(exactly = 0) { mockAccountRepository.updateAccountBalance(any()) }

        assertThat(result).isEqualTo(Resource.Success(Unit))
    }

}
