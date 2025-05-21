package com.example.rateswap.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val currency: String,
    val balance: Double,
)