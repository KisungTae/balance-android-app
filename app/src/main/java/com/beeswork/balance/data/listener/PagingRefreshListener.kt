package com.beeswork.balance.data.listener

import com.beeswork.balance.data.database.response.PagingRefresh

interface PagingRefreshListener<T> {
    fun onRefresh(element: PagingRefresh<T>)
}