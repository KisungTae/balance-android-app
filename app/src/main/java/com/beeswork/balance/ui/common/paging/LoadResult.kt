package com.beeswork.balance.ui.common.paging

sealed class LoadResult<Key: Any, Value: Any> {

    data class Error<Key: Any, Value : Any>(
        val loadType: LoadType,
        val throwable: Throwable?
    ) : LoadResult<Key, Value>()

    data class Success<Key: Any, Value : Any>(
        val items: List<Value>,
        val loadParam: LoadParam<Key>
    ) : LoadResult<Key, Value>() {

        fun reachedEnd(): Boolean {
            return items.size < loadParam.loadSize
        }
    }

}