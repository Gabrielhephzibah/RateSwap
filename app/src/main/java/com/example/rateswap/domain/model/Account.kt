package com.example.rateswap.domain.model

data class Account(
    val id: Int,
    val currency: String,
    val balance: Double,
)