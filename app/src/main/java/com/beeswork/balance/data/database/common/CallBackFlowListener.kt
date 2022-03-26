package com.beeswork.balance.data.database.common

interface CallBackFlowListener<T> {
    fun onInvoke(data: T)
}