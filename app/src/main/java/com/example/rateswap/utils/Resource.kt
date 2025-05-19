package com.example.rateswap.utils

sealed class Resource<out T>(
    val data: T? = null,
    val apiError: Throwable? = null,
) {
    data class Success<T>(val mData: T): Resource<T>(data = mData)
    data class Error(val mError: Throwable): Resource<Nothing>(apiError = mError)
}