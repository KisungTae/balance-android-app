package com.beeswork.balance.ui.common.paging

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class Pager<T: Any>(
    private val pageSize: Int,
    private val coroutineScope: CoroutineScope,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    private val _pageLiveData = MutableLiveData<Page<T>>()
    val pageLiveData = _pageLiveData




}