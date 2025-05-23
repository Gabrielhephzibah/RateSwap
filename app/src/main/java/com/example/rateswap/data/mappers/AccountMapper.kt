package com.example.rateswap.data.mappers

import com.example.rateswap.data.local.entity.AccountEntity
import com.example.rateswap.domain.model.AccountBalance
import java.util.Locale

fun AccountEntity.toAccount(): AccountBalance{
    return AccountBalance(
        id = id,
        currency = currency,
        balance = balance.toTwoDecimal()
    )
}

fun AccountBalance.toAccountEntity(): AccountEntity{
    return AccountEntity(
        id = id,
        currency = currency,
        balance = balance.toTwoDecimal()
    )
}

fun List<AccountEntity>.toAccountList(): List<AccountBalance> {
    return this.map { it.toAccount() }
}

fun Double.toTwoDecimal(): Double {
    return String.format(
        Locale.getDefault(),
        "%.2f", this).toDouble()
}

