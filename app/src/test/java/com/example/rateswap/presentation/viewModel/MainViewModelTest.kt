package com.example.rateswap.presentation.viewModel

import com.example.rateswap.domain.repository.AccountRepository
import com.example.rateswap.domain.repository.ExchangeRepository
import com.example.rateswap.domain.repository.TransactionRepository
import com.example.rateswap.domain.usecases.CalculateBuyingAccountBalance
import com.example.rateswap.domain.usecases.CalculateCommissionFee
import com.example.rateswap.domain.usecases.CalculateExchangeAmount
import com.example.rateswap.domain.usecases.CalculateSellingAccountBalance
import com.example.rateswap.domain.usecases.ValidateExchangeAmount
import com.example.rateswap.presentation.MainViewModel
import com.example.rateswap.util.TestData
import com.example.rateswap.utils.Resource
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    private lateinit var mainViewModel: MainViewModel
    private val mockExchangeRepository = mockk<ExchangeRepository>()
    private val mockAccountRepository = mockk<AccountRepository>()
    private val mockTransactionRepository = mockk<TransactionRepository>()
    private val mockCalculateExchangeRate = mockk<CalculateExchangeAmount>()
    private val mockValidateExchangeAmount = mockk<ValidateExchangeAmount>()
    private val mockCalculateBuyingAccountBalance = mockk<CalculateBuyingAccountBalance>()
    private val mockCalculateSellingAccountBalance = mockk<CalculateSellingAccountBalance>()
    private val mockCommissionFee = mockk<CalculateCommissionFee>()

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        coEvery { mockAccountRepository.getAccountBalances() } returns flowOf(TestData.accountBalanceList)
        coEvery { mockExchangeRepository.getExchangeRates() } returns flowOf(Resource.Success(TestData.exchangeRate))
        coEvery { mockTransactionRepository.getTransactionCount() } returns flowOf(0)
        coEvery { mockValidateExchangeAmount(any(), any(), any(), any(), any()) } returns flowOf(
            Resource.Success(TestData.AMOUNT.toString())
        )
        coEvery {
            mockCalculateExchangeRate(
                any(),
                any(),
                any(),
                any()
            )
        } returns flowOf(Resource.Success(TestData.AMOUNT))
        coEvery { mockCommissionFee(any(), any()) } returns 0.0

        mainViewModel = MainViewModel(
            exchangeRepository = mockExchangeRepository,
            accountRepository = mockAccountRepository,
            transactionRepository = mockTransactionRepository,
            calculateExchangeRate = mockCalculateExchangeRate,
            validateExchangeAmount = mockValidateExchangeAmount,
            calculateBuyingAccountBalance = mockCalculateBuyingAccountBalance,
            calculateSellingAccountBalance = mockCalculateSellingAccountBalance,
            commissionFee = mockCommissionFee
        )
    }

    @After
    fun tearDown() {
        unmockkAll()

    }

    @Test
    fun `should get accountBalances`() = runTest {
        assertThat(mainViewModel.state.accountBalances).isNotEmpty()
        assertThat(mainViewModel.state.accountBalances).hasSize(2)
        assertThat(mainViewModel.state.accountBalances).isEqualTo(TestData.accountBalanceList)
        coVerify { mockAccountRepository.getAccountBalances() }
    }


    @Test
    fun `should get exchange rates`() = runTest {
        assertThat(mainViewModel.state.exchangeRates).isNotNull()
        assertThat(mainViewModel.state.exchangeRates.rates).isNotEmpty()
        assertThat(mainViewModel.state.exchangeRates.rates["EUR"]).isEqualTo(1.0)
        assertThat(mainViewModel.state.exchangeRates.rates["USD"]).isEqualTo(1.12)
        coVerify { mockExchangeRepository.getExchangeRates() }
    }

    @Test
    fun `should validate exchange amount and calculate exchange rate`() = runTest {
        mainViewModel.updateAmount(TestData.AMOUNT.toString())
        mainViewModel.updateSellingCurrency(TestData.SELLING_CURRENCY)
        mainViewModel.updateBuyingCurrency(TestData.BUYING_CURRENCY)

        advanceTimeBy(301)
        advanceUntilIdle()

        assertThat(mainViewModel.state.validationError).isEmpty()
        assertThat(mainViewModel.state.buyingAmount).isEqualTo(TestData.AMOUNT)
        assertThat(mainViewModel.state.totalAmountDeducted).isEqualTo(TestData.AMOUNT)
        assertThat(mainViewModel.state.commissionFee).isEqualTo(0.0)
        coVerify { mockValidateExchangeAmount.invoke(any(), any(), any(), any(), 0) }
        coVerify { mockCalculateExchangeRate.invoke(any(), any(), any(), any()) }
    }


    @Test
    fun `should validateExchangeAmount, calculateBuyingAccountBalances, calculateSellingAmount and incrementTransactionCount`() =
        runTest {
            coEvery {
                mockCalculateBuyingAccountBalance.invoke(any(), any(), any())
            } returns Resource.Success(Unit)

            coEvery {
                mockCalculateSellingAccountBalance.invoke(any(), any(), any(), any())
            } returns Resource.Success(Unit)

            coEvery { mockTransactionRepository.setTransactionCount(1) } returns Unit

            mainViewModel.submitExchange()
            advanceUntilIdle()
            assertThat(mainViewModel.state.validationError).isEmpty()
            coVerify {
                mockValidateExchangeAmount(any(), any(), any(), any(), any())
            }
            coVerify {
                mockCalculateBuyingAccountBalance.invoke(any(), any(), any())
            }
            coVerify {
                mockCalculateSellingAccountBalance.invoke(any(), any(), any(), any())
            }

            coVerify { mockTransactionRepository.setTransactionCount(1) }

        }
}

