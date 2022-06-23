package com.beeswork.balance.ui.common.paging

sealed class LoadState(
    val loadType: LoadType
) {

    class Loaded(
        loadType: LoadType
    ): LoadState(loadType)

    class Loading(
        loadType: LoadType
    ): LoadState(loadType)

    class Error(
        loadType: LoadType,
        val errorMessage: String
    ): LoadState(loadType)
}