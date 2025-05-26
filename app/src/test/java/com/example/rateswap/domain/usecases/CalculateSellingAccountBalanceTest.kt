package com.example.rateswap.domain.usecases

import com.example.rateswap.domain.repository.AccountRepository
import com.example.rateswap.util.TestData
import com.example.rateswap.utils.Resource
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CalculateSellingAccountBalanceTest {
    private lateinit var calculateSellingAccountBalance: CalculateSellingAccountBalance
    private var mockAccountRepository = mockk<AccountRepository>()
    private var mockCalculateCommissionFee = mockk<CalculateCommissionFee>()

    @Before
    fun setUp() {
        calculateSellingAccountBalance = CalculateSellingAccountBalance(
            accountRepository = mockAccountRepository,
            commissionFee = mockCalculateCommissionFee
        )
    }


    @Test
    fun `should deduct amount and commission from balance and update account when transaction count is greater than 5`() =
        runTest {
            val existingAccount = TestData.accountBalanceEUR
            val accountBalances = listOf(existingAccount)
            val sellingAmount = "50"
            val transactionCount = 6
            val expectedCommission = 0.35

            coEvery {
                mockCalculateCommissionFee(
                    50.0,
                    transactionCount
                )
            } returns expectedCommission
            coJustRun { mockAccountRepository.updateAccountBalance(any()) }

            val result = calculateSellingAccountBalance(
                sellingCurrency = TestData.SELLING_CURRENCY,
                sellingAmount = sellingAmount,
                accountBalances = accountBalances,
                transactionCount = transactionCount
            )

            val expectedUpdatedBalance = 100.0 - (50.0 + expectedCommission)
            val expectedAccount = existingAccount.copy(balance = expectedUpdatedBalance)

            coVerify(exactly = 1) { mockAccountRepository.updateAccountBalance(expectedAccount) }

            assertThat(result).isEqualTo(Resource.Success(Unit))
        }

    @Test
    fun `should deduct amount from balance and update account when transaction count is 0`() =
        runTest {
            val existingAccount = TestData.accountBalanceEUR
            val accountBalances = listOf(existingAccount)
            val sellingAmount = "10"
            val transactionCount = 2

            coEvery { mockCalculateCommissionFee(10.0, transactionCount) } returns 0.0
            coJustRun { mockAccountRepository.updateAccountBalance(any()) }

            val result = calculateSellingAccountBalance(
                sellingCurrency = TestData.SELLING_CURRENCY,
                sellingAmount = sellingAmount,
                accountBalances = accountBalances,
                transactionCount = transactionCount
            )

            val expectedUpdatedBalance = 100.0 - 10.0
            val expectedAccount = existingAccount.copy(balance = expectedUpdatedBalance)

            coVerify { mockAccountRepository.updateAccountBalance(expectedAccount) }
            coVerify(exactly = 1) { mockCalculateCommissionFee(10.0, transactionCount) }

            assertThat(result).isEqualTo(Resource.Success(Unit))
        }

    @Test
    fun `should make invalid selling amount to 0`() = runTest {
        val existingAccount = TestData.accountBalanceEUR
        val balances = listOf(existingAccount)
        val sellingAmount = "0"
        val transactionCount = 2

        coEvery { mockCalculateCommissionFee(0.0, transactionCount) } returns 0.0
        coJustRun { mockAccountRepository.updateAccountBalance(any()) }

        val result = calculateSellingAccountBalance(
            sellingCurrency = TestData.SELLING_CURRENCY,
            sellingAmount = sellingAmount,
            accountBalances = balances,
            transactionCount = transactionCount
        )

        val expectedUpdatedBalance = 100.0 - 0.0
        val expectedAccount = existingAccount.copy(balance = expectedUpdatedBalance)

        coVerify { mockAccountRepository.updateAccountBalance(expectedAccount) }

        assertThat(result).isEqualTo(Resource.Success(Unit))
    }


}