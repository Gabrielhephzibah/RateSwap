package com.example.rateswap.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rateswap.data.local.dao.AccountDao
import com.example.rateswap.data.local.entity.AccountEntity

@Database(
    entities = [AccountEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AccountDatabase: RoomDatabase() {
    abstract fun accountDao(): AccountDao
}