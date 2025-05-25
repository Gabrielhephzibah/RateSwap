package com.example.rateswap.utils

object ErrorMessage {
    const val NO_INTERNET_CONNECTION = "Couldn't reach server. Check your internet connection and try again."
    const val NO_ACCOUNT_FOUND = "You do not have an account in"
    const val AMOUNT_GRATER_THAN_ZERO = "Enter an amount greater than 0"
    const val NOT_ENOUGH_BALANCE = "You do not have enough balance in this account."
    const val SAME_CURRENCY = "You cannot exchange the same currency."
    const val API_ERROR = "Error fetching exchange rates, please try again later"
}