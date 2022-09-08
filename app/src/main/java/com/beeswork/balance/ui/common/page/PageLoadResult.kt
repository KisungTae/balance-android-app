package com.beeswork.balance.ui.common.page

sealed class PageLoadResult<Key: Any, Value: Any> {

    class Error<Key: Any, Value : Any>(
        val pageLoadType: PageLoadType,
        val throwable: Throwable?
    ) : PageLoadResult<Key, Value>()

    class Success<Key: Any, Value : Any>(
        val items: List<Value>,
        val pageLoadParam: PageLoadParam<Key>
    ) : PageLoadResult<Key, Value>() {

        fun reachedEnd(): Boolean {
            return items.size < pageLoadParam.loadSize
        }
    }

}