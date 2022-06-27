package com.beeswork.balance.ui.common.paging

sealed class Page<T : Any, I : Any> {


    data class Error<T: Any, I: Any>(
        val loadType: LoadType,
        val throwable: Throwable?
    ) : Page<T, I>()

    data class Success<T: Any, I: Any>(
        val items: List<T>,
        val firstKey: I?,
        val lastKey: I?,
        val reachedEnd: Boolean,
        val loadType: LoadType
    ) : Page<T, I>()
}