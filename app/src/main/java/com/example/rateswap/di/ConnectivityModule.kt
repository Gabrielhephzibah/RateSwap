package com.example.rateswap.di

import com.example.rateswap.utils.connectivity.ConnectivityObserver
import com.example.rateswap.utils.connectivity.ConnectivityObserverImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ConnectivityModule {

    @Binds
    @Singleton
    abstract fun bindConnectivityObserver(connectivityObserver: ConnectivityObserverImpl): ConnectivityObserver

}