package com.beeswork.balance.ui.common.paging

sealed class LoadState {

    class Loaded: LoadState()

    class Loading: LoadState()

    class Error(
        val errorMessage: String
    ): LoadState()
}