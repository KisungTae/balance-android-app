package com.beeswork.balance.ui.common.paging

interface PagingAdapterListener {
    fun onPageLoading()
    fun onPageEmpty()
    fun onPageLoaded()
    fun onPageLoadError(throwable: Throwable?)
}