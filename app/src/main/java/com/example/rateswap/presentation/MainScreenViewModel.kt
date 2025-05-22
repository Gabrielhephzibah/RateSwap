package com.example.rateswap.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rateswap.domain.model.ExchangeRate
import com.example.rateswap.domain.repository.AccountRepository
import com.example.rateswap.domain.repository.ExchangeRepository
import com.example.rateswap.domain.usecases.CalculateExchangeRate
import com.example.rateswap.domain.usecases.ValidateExchangeAmount
import com.example.rateswap.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val exchangeRepository: ExchangeRepository,
    private val calculateExchangeRate: CalculateExchangeRate,
    private val accountRepository: AccountRepository,
    private val validateExchangeAmount: ValidateExchangeAmount
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
        validationError = ""
    }

    fun updateAmountToReceive(){
        state = state.copy(
            amountToReceive = 0.0
        )
    }




    init {
        getAccountBalance()
        getExchangeRate()
        calculateExchangeRate()
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

    private fun calculateExchangeRate(){
        viewModelScope.launch {
             sellingAmount.debounce(300)
                 .distinctUntilChanged()
                 .flatMapLatest { amount ->
                        validateExchangeAmount(
                            amount = amount,
                            sellingCurrency = sellingCurrency,
                            buyingCurrency = buyingCurrency,
                            accountBalance = state.accountBalances
                        )
                 }
                 .flatMapLatest { validatedAmount ->
                     if (validatedAmount is Resource.Error) {
                         return@flatMapLatest flowOf(validatedAmount)
                     }
                   calculateExchangeRate(validatedAmount.data, sellingCurrency, buyingCurrency, state.exchangeRates)
                 }.collect {
                        when(it){
                            is Resource.Error -> {
                                validationError = it.mError.message.toString()
                                println(" ZIBAH::Validation Error: ${it.mError.message}")
                            }
                            is Resource.Success -> {
                                println("ZIBAH:: Success: ${it.data}")
                                state = state.copy(
                                    amountToReceive = it.data ?: 0.0
                                )
                            }
                        }
                 }
        }
    }

   private fun getExchangeRate(){
        viewModelScope.launch {
            exchangeRepository.getExchangeRates().collect { resource ->
                when(resource){
                    is Resource.Error -> {
                        println("ZIBAH:: Error: ${resource.mError.stackTrace}")
                    }
                    is Resource.Success -> {
                        state = state.copy(
                            exchangeRates = resource.data ?: ExchangeRate(emptyMap()),
                        )
                    }
                }
            }
        }

    }
}