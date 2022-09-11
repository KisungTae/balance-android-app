package com.beeswork.balance.ui.common.page

sealed class PageUIState<Value : Any> {

    data class Error<Value : Any>(
        val items: List<Value>?,
        val pageLoadType: PageLoadType,
        val throwable: Throwable?
    ) : PageUIState<Value>()

    data class Success<Value : Any>(
        val items: List<Value>,
        val pageLoadType: PageLoadType,
        val reachedTop: Boolean,
        val reachedBottom: Boolean
    ) : PageUIState<Value>()

    data class Loading<Value : Any>(
        val items: List<Value>?,
        val pageLoadType: PageLoadType
    ): PageUIState<Value>()

    class Empty<Value: Any>: PageUIState<Value>()
}