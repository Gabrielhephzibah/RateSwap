package com.example.rateswap.data.local.database

import android.util.Log
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.rateswap.data.local.dao.AccountDao
import com.example.rateswap.data.local.entity.AccountEntity
import dagger.Lazy
import dagger.internal.Provider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * A Room database callback class that pre-populates the account database with an initial balance
 * when the database is created for the first time.
 *
 * This callback inserts a default account with a currency of "EUR" and a balance of 1000.00.
 *
 * @param accountDao A lazy provider for the [AccountDao] to perform database operations.
 */
class PrePopulateAccountDatabase(
    private val accountDao: Lazy<AccountDao>
): RoomDatabase.Callback()  {

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.d(
            "PrePopulateAccountDatabase",
            " Failed to pre-populate account database with initial values: ${exception.printStackTrace()}"
        )
    }

    private val coroutineScope = CoroutineScope(SupervisorJob() + exceptionHandler)

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        coroutineScope.launch(Dispatchers.IO) {
            addStartingBalance()
            Log.d("PrePopulateAccountDatabase", "Database pre-populated with starting balance.")
        }
    }

    private suspend fun addStartingBalance() {
        val initialAccount = AccountEntity(
            currency = "EUR",
            balance = 1000.00
        )
        accountDao.get().insertAccount(initialAccount)
    }

}