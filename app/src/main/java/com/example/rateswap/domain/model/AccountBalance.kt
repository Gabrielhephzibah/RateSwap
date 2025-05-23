package com.example.rateswap.domain.model

data class AccountBalance(
    val id: Int = 0,
    val currency: String,
    val balance: Double,
)