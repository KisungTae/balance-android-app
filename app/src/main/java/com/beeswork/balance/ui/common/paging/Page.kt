package com.beeswork.balance.ui.common.paging

class Page<Key : Any, Value : Any>(
    private val pageSize: Int,
    private val numOfPagesToKeep: Int
) {

    private var items: List<Value> = arrayListOf()
    private var firstKey: Key? = null
    private var lastKey: Key? = null
    private var reachedTop: Boolean = false
    private var reachedBottom: Boolean = false


    fun insertAndGeneratePageSnapshot(loadResult: LoadResult<Key, Value>): PageSnapshot<Value> {
        if (loadResult is LoadResult.Success) {
            page.insert(loadResult)

        } else if (loadResult is LoadResult.Error) {

        }
        return PageSnapshot.Error()
    }


    fun getLoadKey(loadType: LoadType): Key? {
        return when (loadType) {
            LoadType.PREPEND, LoadType.REFRESH_PAGE, LoadType.REFRESH_DATA -> firstKey
            LoadType.APPEND, LoadType.APPEND_NEW -> lastKey
            LoadType.INITIAL_LOAD -> null
        }
    }

    fun getLoadSize(loadType: LoadType): Int {
        return when (loadType) {
            LoadType.PREPEND, LoadType.APPEND, LoadType.APPEND_NEW, LoadType.INITIAL_LOAD -> pageSize
            LoadType.REFRESH_DATA, LoadType.REFRESH_PAGE -> {
                if (items.isNotEmpty()) {
                    items.size
                } else {
                    pageSize
                }
            }
        }
    }
}