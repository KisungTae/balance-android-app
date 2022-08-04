package com.beeswork.balance.ui.common.paging

sealed class PageUIState<Value : Any> {

    data class Error<Value : Any>(
        val loadType: LoadType,
        val throwable: Throwable?
    ) : PageUIState<Value>()

    data class Success<Value : Any>(
        val items: List<Value>?,
        val loadType: LoadType,
        val reachedTop: Boolean,
        val reachedBottom: Boolean
    ) : PageUIState<Value>()

    data class Loading<Value : Any>(
        val loadType: LoadType
    ): PageUIState<Value>()


}