package com.beeswork.balance.ui.common.paging

abstract class PagingSource<Key: Any, Value: Any> {

    abstract suspend fun load(loadParam: LoadParam<Key>): LoadResult<Key, Value>

}