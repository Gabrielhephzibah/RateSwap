package com.example.rateswap.utils

import java.util.Locale

fun Double.toTwoDecimal(): Double {
    return String.format(
        Locale.getDefault(),
        "%.2f", this).toDouble()
}