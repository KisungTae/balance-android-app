package com.beeswork.balance.ui.common.paging

interface PagingAdapterListener {
    fun onPageEmpty()
    fun onPageLoaded()
    fun onPageLoadError(throwable: Throwable?)
}