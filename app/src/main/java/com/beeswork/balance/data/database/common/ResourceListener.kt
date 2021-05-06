package com.beeswork.balance.data.database.common

import com.beeswork.balance.data.network.response.Resource

interface ResourceListener<T> {
    fun onInvoke(element: Resource<T>)
}