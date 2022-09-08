package com.beeswork.balance.ui.common.page

import androidx.lifecycle.LiveData

interface Pager<Value: Any> {

    val pageUIStateLiveData: LiveData<Value>

    fun loadPage(pageLoadType: PageLoadType)
}