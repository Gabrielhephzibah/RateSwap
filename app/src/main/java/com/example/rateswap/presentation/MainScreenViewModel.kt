package com.example.rateswap.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rateswap.domain.repository.ExchangeRepository
import com.example.rateswap.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val exchangeRepository: ExchangeRepository,
): ViewModel() {

    var state by mutableStateOf(MainScreenState())
        private set

    init {
        getExchangeRate()
    }

   private fun getExchangeRate(){
        viewModelScope.launch {
            exchangeRepository.getExchangeRates().collect { resource ->
                when(resource){
                    is Resource.Error -> TODO()
                    is Resource.Success -> {
                        println("ZIBAH:: ${resource.data}")
                        state = state.copy(
                            exchangeRates = resource.data ?: emptyMap()
                        )
                    }
                }
            }
        }

    }
}