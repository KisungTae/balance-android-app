package com.beeswork.balance.ui.common.page

data class PageLoadParam<Key: Any>(
    val pageLoadType: PageLoadType,
    val loadKey: Key?,
    val loadSize: Int
)