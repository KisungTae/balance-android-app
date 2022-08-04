package com.beeswork.balance.ui.common.paging

interface Pager {

    suspend fun loadPage(loadType: LoadType)
}