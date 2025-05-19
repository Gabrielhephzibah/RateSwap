package com.example.rateswap.presentation

data class MainScreenState(
     val exchangeRates: Map<String, Double> = emptyMap()
 )