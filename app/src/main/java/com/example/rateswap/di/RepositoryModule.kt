package com.example.rateswap.di

import com.example.rateswap.data.repository.AccountRepositoryImpl
import com.example.rateswap.data.repository.ExchangeRepositoryImpl
import com.example.rateswap.data.repository.TransactionRepositoryImpl
import com.example.rateswap.domain.repository.AccountRepository
import com.example.rateswap.domain.repository.ExchangeRepository
import com.example.rateswap.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun  provideExchangeRepository(exchangeRepository: ExchangeRepositoryImpl): ExchangeRepository

    @Binds
    @Singleton
    abstract fun provideAccountRepository(accountRepository: AccountRepositoryImpl): AccountRepository

    @Binds
    @Singleton
    abstract fun provideTransactionRepository(transactionRepository: TransactionRepositoryImpl): TransactionRepository
}