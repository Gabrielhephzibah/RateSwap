package com.example.rateswap.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rateswap.domain.model.ExchangeRate
import com.example.rateswap.domain.repository.ExchangeRepository
import com.example.rateswap.domain.usecases.CalculateExchangeRate
import com.example.rateswap.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val exchangeRepository: ExchangeRepository,
    private val calculateExchangeRate: CalculateExchangeRate
): ViewModel() {

   var state by mutableStateOf(MainScreenState())
        private set

    private val sellingAmount = MutableStateFlow("")

    var sellingCurrency by mutableStateOf("EUR")
        private set

   var buyingCurrency by mutableStateOf("USD")
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


    init {
        getExchangeRate()
        calculateExchangeRate()
    }

    private fun calculateExchangeRate(){
        viewModelScope.launch {
             sellingAmount.debounce(300)
                 .distinctUntilChanged()
                 .flatMapLatest { amount ->
                   calculateExchangeRate(amount, sellingCurrency, buyingCurrency, state.exchangeRates)
                 }.collect {
                        when(it){
                            is Resource.Error -> TODO()
                            is Resource.Success -> {
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
                    is Resource.Error -> TODO()
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