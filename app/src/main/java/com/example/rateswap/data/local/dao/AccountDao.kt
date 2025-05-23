package com.example.rateswap.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.rateswap.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAccount(account: AccountEntity)


    @Query("SELECT * FROM AccountEntity")
    fun getAllAccounts(): Flow<List<AccountEntity>>

    @Delete
    suspend fun deleteAccount(account: AccountEntity)

    @Query("SELECT * FROM AccountEntity WHERE currency = :currency")
    fun getAccountBalanceByCurrency(currency: String): Flow<AccountEntity?>
}