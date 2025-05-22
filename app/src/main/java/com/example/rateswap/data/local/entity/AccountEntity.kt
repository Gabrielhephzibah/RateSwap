package com.example.rateswap.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val currency: String,
    val balance: Double,
)