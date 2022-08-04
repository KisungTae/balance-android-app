package com.beeswork.balance.ui.common.paging

data class LoadParam<Key: Any>(
    val loadType: LoadType,
    val loadKey: Key?,
    val loadSize: Int
)