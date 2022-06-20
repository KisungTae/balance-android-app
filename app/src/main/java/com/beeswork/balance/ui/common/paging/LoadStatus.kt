package com.beeswork.balance.ui.common.paging

sealed class LoadStatus(
    val loadType: LoadType
) {

    class Loaded(
        loadType: LoadType
    ): LoadStatus(loadType)

    class Loading(
        loadType: LoadType
    ): LoadStatus(loadType)

    class Error(
        loadType: LoadType,
        val errorMessage: String
    ): LoadStatus(loadType)
}