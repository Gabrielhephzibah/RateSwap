package com.example.rateswap.di

import android.content.Context
import androidx.room.Room
import com.example.rateswap.BuildConfig
import com.example.rateswap.data.local.dao.AccountDao
import com.example.rateswap.data.local.database.AccountDatabase
import com.example.rateswap.data.local.database.PrePopulateAccountDatabase
import com.example.rateswap.data.local.datastore.TransactionDataStoreImpl
import com.example.rateswap.data.remote.ExchangeApi
import com.example.rateswap.data.local.datastore.TransactionDataStore
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .callTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideBreedApi(retrofit: Retrofit): ExchangeApi = retrofit.create(ExchangeApi::class.java)

    @Singleton
    @Provides
    fun provideRoomDatabase(
        @ApplicationContext context: Context,
        accountProvider : Lazy<AccountDao>
    ) : AccountDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AccountDatabase::class.java,
            "account-database"
        ).addCallback(PrePopulateAccountDatabase(accountProvider))
            .fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideAccountDao(database: AccountDatabase): AccountDao = database.accountDao()

    @Provides
    @Singleton
    fun provideTransactionDataStore(
        @ApplicationContext context: Context
    ): TransactionDataStore {
        return TransactionDataStoreImpl(context)
    }

    @Singleton
    @Provides
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

}