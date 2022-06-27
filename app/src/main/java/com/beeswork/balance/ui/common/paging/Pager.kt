package com.beeswork.balance.ui.common.paging

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class Pager<T: Any, I: Any>(
    protected val pageSize: Int,
    protected val numOfPagesToKeep: Int,
    protected val coroutineScope: CoroutineScope,
    protected val coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    protected val _pageLiveData = MutableLiveData<Page<T, I>>()
    val pageLiveData = _pageLiveData

    abstract fun load(key: I?, loadType: LoadType)


}