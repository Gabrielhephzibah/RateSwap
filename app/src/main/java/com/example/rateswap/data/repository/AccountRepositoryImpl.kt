package com.example.rateswap.data.repository

import com.example.rateswap.data.local.dao.AccountDao
import com.example.rateswap.data.mappers.toAccount
import com.example.rateswap.data.mappers.toAccountEntity
import com.example.rateswap.domain.model.AccountBalance
import com.example.rateswap.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao
): AccountRepository {

    override fun getAccountBalance(): Flow<List<AccountBalance>> {
        return accountDao.getAllAccounts().map { account ->
            account.map {
                it.toAccount() }
        }
    }

    override suspend fun insertAccountBalance(accountBalance: AccountBalance) {
        accountDao.insertAccount(accountBalance.toAccountEntity())
    }

//    override suspend fun upsertAccountBalance(accountBalance: AccountBalance) {
//        accountDao.upsertAccount(accountBalance.toAccountEntity())
//    }

    override suspend fun updateAccountBalance(accountBalance: AccountBalance) {
        accountDao.updateAccount(accountBalance.toAccountEntity())
    }


}