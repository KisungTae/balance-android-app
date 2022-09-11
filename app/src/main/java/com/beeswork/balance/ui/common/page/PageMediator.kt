package com.beeswork.balance.ui.common.page

import kotlinx.coroutines.flow.StateFlow

interface PageMediator<Value: Any> {

    val pageUIStateFlow: StateFlow<PageUIState<Value>>

    fun loadPage(pageLoadType: PageLoadType)
    fun clearPageLoad(pageLoadType: PageLoadType)
    fun reachedTop(): Boolean
    fun reachedBottom(): Boolean
}