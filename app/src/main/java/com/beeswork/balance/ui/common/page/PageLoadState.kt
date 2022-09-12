package com.beeswork.balance.ui.common.page

sealed class PageLoadState(
    val pageLoadType: PageLoadType
) {
    class Loading(
        pageLoadType: PageLoadType
    ) : PageLoadState(pageLoadType)

    class Loaded(
        val numOfItemsLoaded: Int,
        pageLoadType: PageLoadType
    ) : PageLoadState(pageLoadType)

    class Error(
        val exception: Throwable?,
        pageLoadType: PageLoadType
    ) : PageLoadState(pageLoadType)
}