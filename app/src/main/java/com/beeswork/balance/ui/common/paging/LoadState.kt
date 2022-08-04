package com.beeswork.balance.ui.common.paging

sealed class LoadState {

    object Loaded : LoadState()

    object Loading : LoadState()

    class Error(
        val errorMessage: String?,
        val loadType: LoadType
    ): LoadState()

    object Empty: LoadState()
}