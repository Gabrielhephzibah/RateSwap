package com.example.rateswap.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rateswap.domain.model.ExchangeRate
import com.example.rateswap.domain.repository.AccountRepository
import com.example.rateswap.domain.repository.ExchangeRepository
import com.example.rateswap.domain.repository.TransactionRepository
import com.example.rateswap.domain.usecases.CalculateBuyingAccountBalance
import com.example.rateswap.domain.usecases.CalculateCommissionFee
import com.example.rateswap.domain.usecases.CalculateExchangeAmount
import com.example.rateswap.domain.usecases.CalculateSellingAccountBalance
import com.example.rateswap.domain.usecases.ValidateExchangeAmount
import com.example.rateswap.utils.Resource
import com.example.rateswap.utils.toTwoDecimal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val exchangeRepository: ExchangeRepository,
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val calculateExchangeRate: CalculateExchangeAmount,
    private val validateExchangeAmount: ValidateExchangeAmount,
    private val calculateBuyingAccountBalance: CalculateBuyingAccountBalance,
    private val calculateSellingAccountBalance: CalculateSellingAccountBalance,
    private val commissionFee: CalculateCommissionFee
): ViewModel() {

    var state by mutableStateOf(MainScreenState())
        private set

    var sellingCurrency by mutableStateOf("EUR")
        private set

    var buyingCurrency by mutableStateOf("USD")
        private set

    private val sellingAmount = MutableStateFlow("")

    private var transactionCount by mutableStateOf(0)

    fun updateAmount(amount: String) {
        sellingAmount.value = amount
    }

    fun updateSellingCurrency(currency: String) {
        sellingCurrency = currency
    }

    fun updateBuyingCurrency(currency: String) {
        buyingCurrency = currency
    }

    fun clearValidationError() {
        state = state.copy(
            validationError = ""
        )
    }

    fun updateBuyingAmount() {
        state = state.copy(
            buyingAmount = 0.0
        )
    }

    init {
        getAccountBalances()
        getExchangeRate()
        getTransactionCount()
        calculateExchangeRate()
    }

    private fun getAccountBalances() {
        viewModelScope.launch {
            accountRepository.getAccountBalances().collectLatest { resource ->
                state = state.copy(
                    accountBalances = resource
                )
            }
        }
    }

    private fun getExchangeRate() {
        viewModelScope.launch {
            exchangeRepository.getExchangeRates().collect { resource ->
                state = when (resource) {
                    is Resource.Error -> {
                        state.copy(
                            exchangeRateError = resource.error.toString(),
                        )
                    }

                    is Resource.Success -> {
                        state.copy(
                            exchangeRates = resource.data ?: ExchangeRate(emptyMap()),
                        )
                    }
                }
            }
        }
    }

    private fun calculateExchangeRate() {
        viewModelScope.launch {
            sellingAmount
                .debounce(300)
                .distinctUntilChanged()
                .filter { state.accountBalances.isNotEmpty() }
                .flatMapLatest { amount ->
                    validateExchangeAmount(
                        sellingAmount = amount,
                        sellingCurrency = sellingCurrency,
                        buyingCurrency = buyingCurrency,
                        accountBalances = state.accountBalances,
                        transactionCount = transactionCount
                    )
                }
                .flatMapLatest { validationResult ->
                    if (validationResult is Resource.Error) {
                        return@flatMapLatest flowOf(validationResult)
                    }
                    calculateExchangeRate(
                        validationResult.data,
                        sellingCurrency,
                        buyingCurrency,
                        state.exchangeRates
                    )
                }.collect {
                    state = when (it) {
                        is Resource.Error -> {
                            state.copy(
                                validationError = it.error.toString()
                            )
                        }

                        is Resource.Success -> {
                            state.copy(
                                buyingAmount = it.data ?: 0.0,
                                commissionFee = getCommissionFee().toTwoDecimal(),
                                totalAmountDeducted = getTotalAmountDeducted().toTwoDecimal()
                            )
                        }
                    }
                }
        }
    }

    private fun getTransactionCount() {
        viewModelScope.launch {
            transactionRepository.getTransactionCount().collectLatest{ count ->
                transactionCount = count
            }
        }
    }

    private fun incrementTransactionCount() {
        viewModelScope.launch {
            transactionRepository.setTransactionCount(transactionCount + 1)
        }
    }

    private fun getCommissionFee(): Double {
        return commissionFee(
            amount = sellingAmount.value.toDoubleOrNull() ?: 0.0,
            transactionCount = transactionCount
        )
    }
    private fun getTotalAmountDeducted(): Double {
        return (sellingAmount.value.toDoubleOrNull() ?: 0.0) + getCommissionFee()
    }

    fun submitExchange() {
        viewModelScope.launch {
            validateExchangeAmount(
                sellingAmount = sellingAmount.value,
                sellingCurrency = sellingCurrency,
                buyingCurrency = buyingCurrency,
                accountBalances = state.accountBalances,
                transactionCount = transactionCount
            ).transformLatest { validationResult ->
                if (validationResult is Resource.Error) {
                    emit(validationResult)
                    return@transformLatest
                } else {
                    emit(calculateBuyingAccountBalance(
                        buyingCurrency = buyingCurrency,
                        buyingAmount = state.buyingAmount,
                        accountBalances = state.accountBalances
                    ))
                }
            }.transformLatest { buyingAccount ->
                if (buyingAccount is Resource.Error) {
                    emit(buyingAccount)
                    return@transformLatest
                } else {
                    emit(calculateSellingAccountBalance(
                        sellingCurrency = sellingCurrency,
                        sellingAmount = sellingAmount.value,
                        accountBalances = state.accountBalances,
                        transactionCount = transactionCount
                    ))
                }

            }.collect { resource ->
                when (resource) {
                    is Resource.Error -> {
                        state = state.copy(
                            validationError = resource.error.toString()
                        )
                    }

                    is Resource.Success -> {
                        incrementTransactionCount()
                    }
                }
            }
        }
    }
}