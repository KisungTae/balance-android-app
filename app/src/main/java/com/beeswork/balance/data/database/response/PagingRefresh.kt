package com.beeswork.balance.data.database.response

import com.beeswork.balance.internal.mapper.common.Mapper

class PagingRefresh<out T>(
    val data: T?
) {
    fun<R> map(block: (data: T?) -> R): PagingRefresh<R> {
        return PagingRefresh(block.invoke(data))
    }
}