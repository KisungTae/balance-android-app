package com.beeswork.balance.ui.common.paging

abstract class PagingSource<Key: Any, Value: Any> {

    abstract suspend fun load(key: Key?, loadType: LoadType, loadSize: Int): LoadResult<Key, Value>

}