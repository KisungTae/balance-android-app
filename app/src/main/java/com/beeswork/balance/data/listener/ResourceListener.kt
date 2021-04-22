package com.beeswork.balance.data.listener

import com.beeswork.balance.data.network.response.Resource

interface ResourceListener<T> {
    fun onInvoke(element: Resource<T>)
}