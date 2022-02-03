package com.beeswork.balance.data.database.common

interface InvalidationListener<T> {
    fun onInvalidate(data: T)
}