package com.example.rateswap.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rateswap.data.local.datastore.TransactionDataStore
import com.example.rateswap.domain.model.ExchangeRate
import com.example.rateswap.domain.repository.AccountRepository
import com.example.rateswap.domain.repository.ExchangeRepository
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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val exchangeRepository: ExchangeRepository,
    private val calculateExchangeRate: CalculateExchangeAmount,
    private val accountRepository: AccountRepository,
    private val validateExchangeAmount: ValidateExchangeAmount,
    private val calculateBuyingAccountBalance: CalculateBuyingAccountBalance,
    private val calculateSellingAccountBalance: CalculateSellingAccountBalance,
    private val commissionFee: CalculateCommissionFee
): ViewModel() {

    var state by mutableStateOf(MainScreenState())
        private set

    private val sellingAmount = MutableStateFlow("")

    var sellingCurrency by mutableStateOf("EUR")
        private set

    var buyingCurrency by mutableStateOf("USD")
        private set

    var validationError by mutableStateOf("")
        private set

    var transactionCount = MutableStateFlow(0)
        private set

    fun amountToSell(amount: String) {
        sellingAmount.value = amount
    }

    fun updateSellingCurrency(currency: String) {
        sellingCurrency = currency
    }

    fun updateBuyingCurrency(currency: String) {
        buyingCurrency = currency
    }

    fun clearValidationError() {
        validationError = " "
    }

    fun updateAmountToReceive() {
        state = state.copy(
            amountToReceive = 0.0
        )
    }



    init {
        getAccountBalance()
        getExchangeRate()
        calculateExchangeRate()
        getTransactionCount()
    }

    private fun getAccountBalance() {
        viewModelScope.launch {
            accountRepository.getAccountBalance().collect { resource ->
                println("ZIBAH Account Balance:: $resource")
                state = state.copy(
                    accountBalances = resource
                )
//                when(resource){
//                    is Resource.Error -> TODO()
//                    is Resource.Success -> {
//                        state = state.copy(
//                            accountBalance = resource.data ?: emptyList()
//                        )
//                    }
//                }
            }
        }
    }

    private fun getTransactionCount() {
        viewModelScope.launch {
            accountRepository.getTransactionCount().collectLatest{ count ->
                transactionCount.value = count
            }
        }
    }

    private fun incrementTransactionCount() {
        viewModelScope.launch {
            accountRepository.setTransactionCount(transactionCount.value + 1)
        }
    }

    private fun getCommissionFee(): Double {
        return commissionFee(
            amount = sellingAmount.value.toDoubleOrNull() ?: 0.0,
            transactionCount = transactionCount.value
        )
    }
    private fun getTotalAmountDeducted(): Double {
        return (sellingAmount.value.toDoubleOrNull() ?: 0.0) + getCommissionFee()
    }

    private fun calculateExchangeRate() {
        viewModelScope.launch {
            sellingAmount.debounce(300)
                .distinctUntilChanged()
                .flatMapLatest { amount ->
                    validateExchangeAmount(
                        amount = amount,
                        sellingCurrency = sellingCurrency,
                        buyingCurrency = buyingCurrency,
                        accountBalance = state.accountBalances,
                        transactionCount = transactionCount.value
                    )
                }
                .flatMapLatest { validatedAmount ->
                    if (validatedAmount is Resource.Error) {
                        return@flatMapLatest flowOf(validatedAmount)
                    }
                    calculateExchangeRate(
                        validatedAmount.data,
                        sellingCurrency,
                        buyingCurrency,
                        state.exchangeRates
                    )
                }.collect {
                    when (it) {
                        is Resource.Error -> {
                            validationError = it.mError.message.toString()
                            println(" ZIBAH::Validation Error: ${it.mError.message}")
                        }

                        is Resource.Success -> {
                            println("ZIBAH:: Success: ${it.data}")
                            state = state.copy(
                                amountToReceive = it.data ?: 0.0,
                                commissionFee = getCommissionFee().toTwoDecimal(),
                                totalAmountDeducted = getTotalAmountDeducted().toTwoDecimal()
                            )
                        }
                    }
                }
        }
    }

    private fun getExchangeRate() {
        viewModelScope.launch {
            exchangeRepository.getExchangeRates().collect { resource ->
                when (resource) {
                    is Resource.Error -> {
                        state = state.copy(
                            exchangeRateError = resource.mError.message.toString(),
                        )
                    }

                    is Resource.Success -> {
                        println("UPPERCASE:: ${resource.data}")
                        state = state.copy(
                            exchangeRates = resource.data ?: ExchangeRate(emptyMap()),
                        )
                    }
                }
            }
        }
    }

    fun saveAccountBalance() {
        viewModelScope.launch {
            validateExchangeAmount(
                amount = sellingAmount.value,
                sellingCurrency = sellingCurrency,
                buyingCurrency = buyingCurrency,
                accountBalance = state.accountBalances,
                transactionCount = transactionCount.value
            ).transformLatest { validation ->
                if (validation is Resource.Error) {
                    emit(validation)
                    return@transformLatest
                } else {
                    val buyingAccount = calculateBuyingAccountBalance(
                        buyingCurrency = buyingCurrency,
                        receivingAmount = state.amountToReceive,
                        accountBalance = state.accountBalances
                    )
                    emit(buyingAccount)
                }
            }.transformLatest { buyingAccount ->
                if (buyingAccount is Resource.Error) {
                    emit(buyingAccount)
                    return@transformLatest
                } else {
                    val sellingAccount = calculateSellingAccountBalance(
                        sellingCurrency = sellingCurrency,
                        amountToSell = sellingAmount.value,
                        accountBalance = state.accountBalances,
                        transactionCount = transactionCount.value
                    )
                    emit(sellingAccount)
                }

            }.collect { resource ->
                when (resource) {

                    is Resource.Error -> {
                        validationError = resource.mError.message.toString()
                        println("ZIBAH:: ERROR: ${resource.mError.message.toString()}")
                    }

                    is Resource.Success -> {
                        incrementTransactionCount()
                        println("ZIBAH:: Success: Account Added Successfully")
                    }
                }
            }
        }
    }
}