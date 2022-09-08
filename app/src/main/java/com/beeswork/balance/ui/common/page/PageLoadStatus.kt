package com.beeswork.balance.ui.common.page

sealed class PageLoadStatus(
    val pageLoadType: PageLoadType
){

    class Loading(
        pageLoadType: PageLoadType
    ) : PageLoadStatus(pageLoadType)

    class Error(
        pageLoadType: PageLoadType,
        val errorMessage: String?
    ): PageLoadStatus(pageLoadType)

    class Loaded(
        pageLoadType: PageLoadType,
        val pageEmpty: Boolean
    ): PageLoadStatus(pageLoadType)
}