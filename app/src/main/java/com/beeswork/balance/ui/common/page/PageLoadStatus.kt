package com.beeswork.balance.ui.common.page

sealed class PageLoadStatus(
    val pageLoadType: PageLoadType
) {
    class Loading(
        pageLoadType: PageLoadType
    ) : PageLoadStatus(pageLoadType)

    class Loaded(
        pageLoadType: PageLoadType,
        val numOfItemsLoaded: Int
    ) : PageLoadStatus(pageLoadType)

    class Error(
        pageLoadType: PageLoadType,
        val exception: Throwable?
    ) : PageLoadStatus(pageLoadType)
}