package com.example.rateswap.data.local.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/** * Implementation of TransactionDataStore that uses DataStore to persist transaction count.
 * * @param context Application context to access DataStore.
 */

@Singleton
 class TransactionDataStoreImpl @Inject constructor(
    private val context: Context
): TransactionDataStore {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "transaction_preferences")

    override fun getTransactionCount(): Flow<Int> {
        return context.dataStore.data.catch { e ->
            emit(emptyPreferences())
            Log.e("TransactionDataStore", "Error reading transaction count", e)
        }.map { preference ->
            preference[transactionCount] ?: 0
        }
    }

    override suspend fun setTransactionCount(count: Int) {
        context.dataStore.edit { mutablePreferences ->
            mutablePreferences[transactionCount] = count
        }
    }

    companion object{
        val transactionCount  = intPreferencesKey("transaction_count")
    }

}