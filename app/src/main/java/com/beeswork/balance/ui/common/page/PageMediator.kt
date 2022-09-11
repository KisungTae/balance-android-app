package com.beeswork.balance.ui.common.page

import androidx.lifecycle.LiveData

interface PageMediator<Value: Any> {

    val pageUIStateLiveData: LiveData<PageUIState<Value>>

    fun loadPage(pageLoadType: PageLoadType)
    fun clearPageLoad(pageLoadType: PageLoadType)
    fun reachedTop(): Boolean
    fun reachedBottom(): Boolean
}