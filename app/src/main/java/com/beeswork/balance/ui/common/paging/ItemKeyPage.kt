package com.beeswork.balance.ui.common.paging

import kotlin.math.max


class ItemKeyPage<Key : Any, Value : ItemKeyPageable<Key>>(
    private val pageSize: Int,
    private val maxPageSize: Int
) {

    private var items: List<Value> = arrayListOf()
    private var reachedTop: Boolean = true
    private var reachedBottom: Boolean = true
    private var failedRefreshPrependDataLoadKey: Key? = null


    fun mergeLoadResult(loadResult: LoadResult<Key, Value>): PageUIState<Value> {
        return when (loadResult) {
            is LoadResult.Error -> {
                PageUIState.Error(loadResult.loadType, loadResult.throwable)
            }
            is LoadResult.Success -> {
                updatePageEnds(loadResult)
                updateItems(loadResult)
                PageUIState.Success(items, loadResult.loadParam.loadType, reachedTop, reachedBottom)
            }
        }
    }

    private fun updateItems(loadResult: LoadResult.Success<Key, Value>) {
        val tempItems = mutableListOf<Value>()
        tempItems.addAll(loadResult.items)
        when (loadResult.loadParam.loadType) {
            LoadType.PREPEND_DATA -> {
                tempItems.addAll(items.take((maxPageSize - tempItems.size)))
            }
            LoadType.APPEND_DATA -> {
                tempItems.addAll(0, items.takeLast((maxPageSize - tempItems.size)))
            }
            LoadType.REFRESH_DATA, LoadType.REFRESH_PAGE, LoadType.REFRESH_FIRST_PAGE, LoadType.PREPEND_DATA_AFTER_EMPTY_REFRESH -> { }
        }
        items = tempItems.toList()
    }

    private fun updatePageEnds(loadResult: LoadResult.Success<Key, Value>) {
        when (loadResult.loadParam.loadType) {
            LoadType.REFRESH_PAGE, LoadType.REFRESH_FIRST_PAGE -> {
                return
            }
            LoadType.PREPEND_DATA -> {
                reachedTop = loadResult.reachedEnd()
                if (exceedMaxPageSize(loadResult.items)) {
                    reachedBottom = false
                }
            }
            LoadType.APPEND_DATA -> {
                reachedBottom = loadResult.reachedEnd()
                if (exceedMaxPageSize(loadResult.items)) {
                    reachedTop = false
                }
            }
            LoadType.REFRESH_DATA -> {
                if (loadResult.reachedEnd()) {
                    reachedBottom = true
                }
            }
            LoadType.PREPEND_DATA_AFTER_EMPTY_REFRESH -> {
                reachedTop = loadResult.reachedEnd()
            }
        }
    }

    private fun exceedMaxPageSize(newItems: List<Value>): Boolean {
        return (newItems.size + items.size) > maxPageSize
    }

    private fun getLoadKey(loadType: LoadType): Key? {
        return when (loadType) {
            LoadType.PREPEND_DATA_AFTER_EMPTY_REFRESH -> failedRefreshPrependDataLoadKey
            LoadType.PREPEND_DATA, LoadType.REFRESH_PAGE, LoadType.REFRESH_DATA -> items.firstOrNull()?.key
            LoadType.APPEND_DATA -> items.lastOrNull()?.key
            LoadType.REFRESH_FIRST_PAGE -> null
        }
    }

    private fun getLoadSize(loadType: LoadType): Int {
        return when (loadType) {
            LoadType.PREPEND_DATA, LoadType.APPEND_DATA, LoadType.PREPEND_DATA_AFTER_EMPTY_REFRESH -> pageSize
            LoadType.REFRESH_DATA, LoadType.REFRESH_PAGE, LoadType.REFRESH_FIRST_PAGE -> max(items.size, pageSize)
        }
    }

    fun shouldClearRefreshPrependDataError(): Boolean {
        return failedRefreshPrependDataLoadKey != null
    }

    fun getLoadParam(loadType: LoadType): LoadParam<Key> {
        if ((loadType != LoadType.REFRESH_DATA && items.isEmpty())
            || (loadType == LoadType.PREPEND_DATA_AFTER_EMPTY_REFRESH && failedRefreshPrependDataLoadKey == null)
        ) {
            return LoadParam(LoadType.REFRESH_DATA, null, pageSize)
        }
        return LoadParam(loadType, getLoadKey(loadType), getLoadSize(loadType))
    }
}