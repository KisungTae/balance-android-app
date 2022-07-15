package com.beeswork.balance.ui.common.paging


class ItemKeyPage<Key : Any, Value : Any>(
    private val pageSize: Int,
    private val numOfPagesToKeep: Int
) {

    private var items: List<Value> = arrayListOf()
    private var firstKey: Key? = null
    private var lastKey: Key? = null
    private var reachedTop: Boolean = false
    private var reachedBottom: Boolean = false


    fun insertAndGeneratePageSnapshot(loadResult: LoadResult.Success<Key, Value>): PageSnapshot<Value> {



//        val newItems = loadResult.items
//        when (loadResult.loadType) {
//
//            LoadType.PREPEND -> {
//                reachedTop = newItems.size < pageSize
//                0
//            }
//            LoadType.APPEND -> {
//
//            }
//            LoadType.APPEND_NEW -> TODO()
//            LoadType.INITIAL_LOAD -> TODO()
//            LoadType.REFRESH_DATA -> TODO()
//            LoadType.REFRESH_PAGE -> TODO()
//        }
        return PageSnapshot.Success(items, loadResult.loadType, reachedTop, reachedBottom)
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