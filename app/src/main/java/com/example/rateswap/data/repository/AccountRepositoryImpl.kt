package com.example.rateswap.data.repository

import com.example.rateswap.data.local.dao.AccountDao
import com.example.rateswap.data.mappers.toAccount
import com.example.rateswap.domain.model.Account
import com.example.rateswap.domain.repository.AccountRepository
import com.example.rateswap.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao
): AccountRepository {

    override fun getAccountBalance(): Flow<List<Account>> {
        return accountDao.getAllAccounts().map { account ->
            account.map { it.toAccount() } }

    }

    override fun insertAccountBalance(balance: Double) {
        TODO("Not yet implemented")
    }


}