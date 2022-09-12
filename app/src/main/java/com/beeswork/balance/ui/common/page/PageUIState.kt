package com.beeswork.balance.ui.common.page

class PageUIState<Value : Any>(
    val items: List<Value>?,
    val pageLoadState: PageLoadState
)