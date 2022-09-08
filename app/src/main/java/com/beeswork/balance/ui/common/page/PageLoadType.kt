package com.beeswork.balance.ui.common.page

enum class PageLoadType {
    PREPEND_DATA,
    APPEND_DATA,
    REFRESH_DATA,
    REFRESH_PAGE,
    REFRESH_FIRST_PAGE,
    REFRESH_PREPEND_DATA;

    fun isAppend(): Boolean {
        return this == APPEND_DATA || this == REFRESH_DATA || this == REFRESH_PAGE
    }

    fun isIncludeLoadKey(): Boolean {
        return this == REFRESH_DATA || this == REFRESH_PAGE
    }
}