package com.beeswork.balance.ui.common.paging

enum class LoadType {
    PREPEND,
    APPEND,
    APPEND_NEW,
    INITIAL_LOAD,
    REFRESH_DATA,
    REFRESH_PAGE;


    fun isAppend(): Boolean {
        return this == APPEND || this == INITIAL_LOAD || this == REFRESH_DATA || this == REFRESH_PAGE
    }

    fun isIncludeLoadKey(): Boolean {
        return this == REFRESH_DATA || this == REFRESH_PAGE
    }
}