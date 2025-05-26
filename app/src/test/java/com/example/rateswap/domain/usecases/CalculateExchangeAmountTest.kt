package com.example.rateswap.domain.usecases

import app.cash.turbine.test
import com.example.rateswap.domain.model.ExchangeRate
import com.example.rateswap.util.TestData
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CalculateExchangeAmountTest {
    private lateinit var calculateExchangeAmount: CalculateExchangeAmount

    @Before
    fun setUp() {
        calculateExchangeAmount = CalculateExchangeAmount()
    }

    @Test
    fun `should return correct exchange amount for given rate and amount`() = runTest {
        val exchangeRate = TestData.exchangeRate

        calculateExchangeAmount(
            amount = TestData.AMOUNT.toString(),
            sellingCurrency = TestData.SELLING_CURRENCY,
            buyingCurrency = TestData.BUYING_CURRENCY,
            exchangeRate = exchangeRate
        ).test {
            val result = awaitItem()
            assertThat(result.data).isEqualTo(112.0)
            awaitComplete()
        }
    }

    @Test
    fun `returns 0 when amount is null`() = runTest {
        val exchangeRate = TestData.exchangeRate

        calculateExchangeAmount(
            amount = null,
            sellingCurrency = TestData.SELLING_CURRENCY,
            buyingCurrency = TestData.BUYING_CURRENCY,
            exchangeRate = exchangeRate
        ).test {
            val result = awaitItem()
            assertThat(result.data).isEqualTo(0.0)
            awaitComplete()
        }
    }

    @Test
    fun `returns 0 when rate is missing for currencies`() = runTest {
        val exchangeRate = ExchangeRate(rates = emptyMap())

        calculateExchangeAmount(
            amount = TestData.AMOUNT.toString(),
            sellingCurrency = TestData.SELLING_CURRENCY,
            buyingCurrency = TestData.BUYING_CURRENCY,
            exchangeRate = exchangeRate
        ).test {
            val result = awaitItem()
            assertThat(result.data).isEqualTo(0.0)
            awaitComplete()
        }
    }

    @Test
    fun `returns 0 when amount is not a number`() = runTest {
        val exchangeRate = TestData.exchangeRate

        calculateExchangeAmount(
            amount = "amount",
            sellingCurrency = TestData.SELLING_CURRENCY,
            buyingCurrency = TestData.BUYING_CURRENCY,
            exchangeRate = exchangeRate
        ).test {
            val result = awaitItem()
            assertThat(result.data).isEqualTo(0.0)
            awaitComplete()
        }
    }
}