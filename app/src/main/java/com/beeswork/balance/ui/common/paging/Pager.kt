package com.beeswork.balance.ui.common.paging

import androidx.lifecycle.MutableLiveData

abstract class Pager<T: Any>(

) {

    private val _pageLiveData = MutableLiveData<Page<T>>()
    val pageLiveData = _pageLiveData




}