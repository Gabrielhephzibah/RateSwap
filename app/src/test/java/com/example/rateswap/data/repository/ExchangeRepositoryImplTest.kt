package com.example.rateswap.data.repository

import app.cash.turbine.test
import com.example.rateswap.data.remote.ExchangeApi
import com.example.rateswap.domain.repository.ExchangeRepository
import com.example.rateswap.util.TestData
import com.example.rateswap.utils.ErrorMessage
import com.example.rateswap.utils.connectivity.ConnectivityObserver
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExchangeRepositoryImplTest {
    private lateinit var exchangeRepository: ExchangeRepository
    private val mockExchangeApi = mockk<ExchangeApi>()
    private val mockConnectivityObserver = mockk<ConnectivityObserver>()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        exchangeRepository =
            ExchangeRepositoryImpl(mockExchangeApi, mockConnectivityObserver, testDispatcher)
    }

    @Test
    fun `getExchangeRates returns exchange rates`() = runTest {

        coEvery { mockExchangeApi.getExchangeRates() } returns TestData.exchangeRatesDto
        every { mockConnectivityObserver.isConnected } returns true

        exchangeRepository.getExchangeRates().test {
            val result = awaitItem()
            assertThat(result).isNotNull()
            assertThat(result.data).isEqualTo(TestData.exchangeRate)
            cancelAndIgnoreRemainingEvents()
        }

    }

    @Test
    fun `getExchangeRates emits error when no internet connection`() = runTest {
        every { mockConnectivityObserver.isConnected } returns false

        exchangeRepository.getExchangeRates().test {
            val result = awaitItem()
            assertThat(result.error).isEqualTo(ErrorMessage.NO_INTERNET_CONNECTION)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getExchangeRates emits error when API call fails`() = runTest {
        coEvery { mockExchangeApi.getExchangeRates() } throws Exception("API Error")
        every { mockConnectivityObserver.isConnected } returns true

        exchangeRepository.getExchangeRates().test {
            val result = awaitItem()
            assertThat(result.error).isEqualTo(ErrorMessage.API_ERROR)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

