package com.example.rateswap.data.mappers

import com.example.rateswap.data.local.entity.AccountEntity
import com.example.rateswap.domain.model.AccountBalance
import com.example.rateswap.utils.toTwoDecimal

fun AccountEntity.toAccountBalance(): AccountBalance{
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



