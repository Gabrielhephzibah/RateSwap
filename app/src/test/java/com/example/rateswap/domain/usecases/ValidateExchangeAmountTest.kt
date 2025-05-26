package com.example.rateswap.domain.usecases

import app.cash.turbine.test
import com.example.rateswap.util.TestData
import com.example.rateswap.utils.ErrorMessage
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ValidateExchangeAmountTest {
    private lateinit var validateExchangeAmount: ValidateExchangeAmount
    private val mockCalculateCommissionFee = mockk<CalculateCommissionFee>()

    @Before
    fun setUp() {
        validateExchangeAmount = ValidateExchangeAmount(mockCalculateCommissionFee)
    }

    @Test
    fun `returns error when no account is found`() = runTest {
        val accountBalances = TestData.accountBalanceList

        validateExchangeAmount(
            sellingAmount = TestData.AMOUNT.toString(),
            sellingCurrency = "GBP",
            buyingCurrency = TestData.BUYING_CURRENCY,
            accountBalances = accountBalances,
            transactionCount = 1
        ).test {
            val item = awaitItem()
            assertThat(item.error).isEqualTo("${ErrorMessage.NO_ACCOUNT_FOUND} GBP.")
            awaitComplete()
        }
    }

    @Test
    fun `returns error when amount is zero`() = runTest {
        val accountBalance = TestData.accountBalanceList
        validateExchangeAmount(
            sellingAmount = "0",
            sellingCurrency = TestData.SELLING_CURRENCY,
            buyingCurrency = TestData.BUYING_CURRENCY,
            accountBalances = accountBalance,
            transactionCount = 1
        ).test {
            val item = awaitItem()
            assertThat(item.error).isEqualTo(ErrorMessage.AMOUNT_GRATER_THAN_ZERO)
            awaitComplete()
        }

    }

    @Test
    fun `returns error when not enough balance`() = runTest {
        val account = TestData.accountBalanceEUR
        coEvery { mockCalculateCommissionFee(200.0, 1) } returns 1.0

        validateExchangeAmount(
            sellingAmount = "200",
            sellingCurrency = TestData.SELLING_CURRENCY,
            buyingCurrency = TestData.BUYING_CURRENCY,
            accountBalances = listOf(account),
            transactionCount = 1
        ).test {
            val item = awaitItem()
            assertThat(item.error).isEqualTo(ErrorMessage.NOT_ENOUGH_BALANCE)
            awaitComplete()
        }
        coVerify { mockCalculateCommissionFee(200.0, 1) }
    }

    @Test
    fun `returns error when currencies are the same`() = runTest {
        val account = TestData.accountBalanceEUR
        coEvery { mockCalculateCommissionFee(100.0, 1) } returns 0.0

        val result = validateExchangeAmount(
            sellingAmount = TestData.AMOUNT.toString(),
            sellingCurrency = TestData.SELLING_CURRENCY,
            buyingCurrency = "EUR",
            accountBalances = listOf(account),
            transactionCount = 1
        )

        result.test {
            val item = awaitItem()
            assertThat(item.error).isEqualTo(ErrorMessage.SAME_CURRENCY)
            awaitComplete()
        }
        coVerify { mockCalculateCommissionFee(100.0, 1) }
    }

    @Test
    fun `returns success when all validations pass`() = runTest {
        val account = TestData.accountBalanceEUR
        coEvery { mockCalculateCommissionFee(50.0, 1) } returns 1.0

        validateExchangeAmount(
            sellingAmount = "50",
            sellingCurrency = TestData.SELLING_CURRENCY,
            buyingCurrency = TestData.BUYING_CURRENCY,
            accountBalances = listOf(account),
            transactionCount = 1
        ).test {
            val item = awaitItem()
            assertThat(item.data).isEqualTo("50")
            awaitComplete()
        }
        coVerify { mockCalculateCommissionFee(50.0, 1) }
    }

}