package com.example.rateswap.data.mappers

import com.example.rateswap.data.local.entity.AccountEntity
import com.example.rateswap.domain.model.Account
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

fun AccountEntity.toAccount(): Account{
    return Account(
        id = id,
        currency = currency,
        balance = balance
    )
}

fun Account.toAccountEntity(): AccountEntity{
    return AccountEntity(
        id = id,
        currency = currency,
        balance = balance
    )
}

fun List<AccountEntity>.toAccountList(): List<Account> {
    return this.map { it.toAccount() }
}

fun Double.toTwoDecimal(): Double {
    return String.format(
        Locale.getDefault(),
        "%.2f", this).toDouble()
}

