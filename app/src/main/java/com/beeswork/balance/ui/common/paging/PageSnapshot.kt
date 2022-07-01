package com.beeswork.balance.ui.common.paging

sealed class PageSnapshot<Value : Any> {

    data class Error<Value : Any>(
        val loadType: LoadType,
        val throwable: Throwable?
    ) : PageSnapshot<Value>()

    data class Success<Value : Any>(
        val items: List<Value>?,
        val loadType: LoadType,
        val reachedTop: Boolean,
        val reachedBottom: Boolean
    ) : PageSnapshot<Value>()


}