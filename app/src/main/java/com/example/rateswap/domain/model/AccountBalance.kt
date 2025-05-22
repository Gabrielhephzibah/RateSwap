package com.example.rateswap.domain.model

data class AccountBalance(
    val id: Int,
    val currency: String,
    val balance: Double,
)