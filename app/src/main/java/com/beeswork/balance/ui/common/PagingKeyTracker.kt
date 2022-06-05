package com.beeswork.balance.ui.common

import androidx.paging.PagingSource

class PagingKeyTracker<Value: Any> {

    private val pagingKeys = mutableSetOf<Int>()
    var prevKey: Int? = null
        private set

    var currKey: Int? = null
        private set

    // assume pageSize == prefetchDistance, ratio of 1:1
    fun addRefreshedPageKeys(anchorPage: PagingSource.LoadResult.Page<Int, Value>?): Int {
        if (anchorPage ==  null || (anchorPage.prevKey == null && anchorPage.nextKey == null)) {
            return 0
        }

        anchorPage.prevKey?.let { _prevKey ->
            pagingKeys.add(_prevKey)
            prevKey = _prevKey
        }

        anchorPage.nextKey?.let { nextKey ->
            pagingKeys.add(nextKey)
        }

        currKey = anchorPage.prevKey?.plus(1) ?: anchorPage.nextKey?.minus(1)

        if (prevKey == null) {
            prevKey = currKey
        }
        return pagingKeys.size
    }

    fun shouldSyncPage(pageKey: Int): Boolean {
        return !pagingKeys.remove(pageKey)
    }

}