package com.beeswork.balance.data.database.common

interface PageInvalidationListener<T> {
    fun onInvalidate(data: T?)
}