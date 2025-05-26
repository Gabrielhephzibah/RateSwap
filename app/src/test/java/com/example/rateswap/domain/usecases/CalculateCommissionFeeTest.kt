package com.example.rateswap.domain.usecases

import com.example.rateswap.util.TestData
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class CalculateCommissionFeeTest {
    private lateinit var calculateCommissionFee: CalculateCommissionFee

    @Before
    fun setUp() {
        calculateCommissionFee = CalculateCommissionFee()
    }

    @Test
    fun `should return 0 commission if transaction count is less than limit`() {
        val amount = TestData.AMOUNT
        val transactionCount = 3

        val result = calculateCommissionFee(amount, transactionCount)
        assertThat(result).isEqualTo(0.00)
    }

    @Test
    fun `should return 0 commission if transaction count equals the limit`() {
        val amount = TestData.AMOUNT
        val transactionCount = 5

        val result = calculateCommissionFee(amount, transactionCount)
        assertThat(result).isEqualTo(0.007 * amount)
    }

    @Test
    fun `should return commission if transaction count is greater than limit`() {
        val amount = TestData.AMOUNT
        val transactionCount = 10

        val result = calculateCommissionFee(amount, transactionCount)
        assertThat(result).isEqualTo(TestData.COMMISSION_PERCENTAGE * amount)
    }


}