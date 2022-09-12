package com.beeswork.balance.ui.common.page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

class Pager<Key : Any, Value : Any>(
    private val pageSize: Int,
    private val maxPageSize: Int,
    private val pageSource: PageSource<Key, Value>,
    private val coroutineScope: CoroutineScope,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
): PageMediator<Value> {


    private val _pageUIStateLiveData = MutableLiveData<PageUIState<Value>>()
    override val pageUIStateLiveData: LiveData<PageUIState<Value>> get() = _pageUIStateLiveData

    private var reachedTop = false
    private var reachedBottom = false




    override fun loadPage(pageLoadType: PageLoadType) {
        TODO("Not yet implemented")
    }

    override fun clearPageLoad(pageLoadType: PageLoadType) {
        TODO("Not yet implemented")
    }

    override fun reachedTop(): Boolean {
        return reachedTop
    }

    override fun reachedBottom(): Boolean {
        return reachedBottom
    }

    fun withHeader(block: () -> Value): Pager<Key, Value> {
        return this
    }

    fun withFooter(block: () -> Value): Pager<Key, Value> {
        return this
    }

    fun withLoadStateLoading(block: (pageLoadType: PageLoadType) -> Value): Pager<Key, Value> {
        return this
    }

    fun withLoadStateError(block: (pageLoadType: PageLoadType, exception: Throwable?) -> Value): Pager<Key, Value> {
        return this
    }

    companion object {
        const val LOAD_PAGE_CHANNEL_BUFFER = 10
    }






// todo: 1. keep the loading state when refresh or other data load
//       2. if error load state at the bottom but successful prepend, then get rid of the bottom error state
//       3. every data load, needs page_refresh after it successfully loads data and udpate data in database
//       4.

//    override val pageUIStateLiveData: LiveData<Value>
//        get() = TODO("Not yet implemented")
    //    private var items: List<Value> = arrayListOf()
//    private var reachedTop: Boolean = true
//    private var reachedBottom: Boolean = true
//    private var refreshPrependDataLoadKey: Key? = null
//
//    private var header: Value? = null
//    private var footer: Value? = null






//    private val _pageUIStateLiveData = MutableLiveData<PageUIState<Value>>()
//    private val pageUIStateLiveData = _pageUIStateLiveData
//
//    private val mutex = Mutex()
//    private val loadTypeQueue = mutableSetOf<PageLoadType>()
//    private val pageLoadEventChannel = Channel<PageLoadType>(LOAD_PAGE_CHANNEL_BUFFER)
//
//    val pagingMediator = PageMediator(this@Pager, pageUIStateLiveData)
//
//    init {
//        pageLoadEventChannel.consumeAsFlow().onEach { loadType ->
//            if (refreshPrependDataLoadKey != null && loadType != PageLoadType.REFRESH_PREPEND_DATA) {
//                _pageUIStateLiveData.postValue(PageUIState.Error(loadType, PageLoadType.REFRESH_PREPEND_DATA, null))
//            }
//
//            val loadResult = doLoadPage(loadType)
//            if (loadResult is PageLoadResult.Success
//                && (loadResult.pageLoadParam.pageLoadType == PageLoadType.REFRESH_DATA || loadResult.pageLoadParam.pageLoadType == PageLoadType.REFRESH_PAGE)
//                && loadResult.pageLoadParam.loadKey != null
//                && loadResult.items.isNullOrEmpty()
//            ) {
//                refreshPrependDataLoadKey = loadResult.pageLoadParam.loadKey
//                doLoadPage(PageLoadType.REFRESH_PREPEND_DATA)
//            }
//        }.launchIn(coroutineScope + defaultDispatcher)
//    }
//
//    fun loadPage(pageLoadType: PageLoadType) {
//        coroutineScope.launch {
//            mutex.withLock {
//                if (!loadTypeQueue.contains(pageLoadType)) {
//                    loadTypeQueue.add(pageLoadType)
//                    pageLoadEventChannel.send(pageLoadType)
//                }
//            }
//        }
//    }
//
//    private suspend fun doLoadPage(pageLoadType: PageLoadType): PageLoadResult<Key, Value> {
//        val loadParam = getLoadParam(pageLoadType)
//        if (loadParam.pageLoadType == PageLoadType.REFRESH_DATA || loadParam.pageLoadType == PageLoadType.REFRESH_PREPEND_DATA) {
//            _pageUIStateLiveData.postValue(PageUIState.Loading(pageLoadType))
//        }
//        delay(3000)
//        val loadResult = pageSource.load(loadParam)
//        val pageUIState = when (loadResult) {
//            is PageLoadResult.Error -> {
//                PageUIState.Error(pageLoadType, loadResult.pageLoadType, loadResult.throwable)
//            }
//            is PageLoadResult.Success -> {
//                val tempItems = mergeLoadResult(loadResult)
//
//                if (tempItems.isNotEmpty()) {
//                    //todo: comment in again
////                    addHeaderAndFooter(tempItems)
//                }
//                PageUIState.Success(tempItems, pageLoadType, loadResult.pageLoadParam.pageLoadType, reachedTop, reachedBottom)
//            }
//        }
//        _pageUIStateLiveData.postValue(pageUIState)
//        mutex.withLock {
//            loadTypeQueue.remove(pageLoadType)
//        }
//        return loadResult
//    }
//
//    private fun addHeaderAndFooter(items: MutableList<Value>) {
//        if (reachedTop) {
//            header?.let { _header ->
//                items.add(0, _header)
//            }
//        }
//        if (reachedBottom) {
//            footer?.let { _footer ->
//                items.add(_footer)
//            }
//        }
//    }
//
//    private fun mergeLoadResult(pageLoadResult: PageLoadResult.Success<Key, Value>): MutableList<Value> {
//        val tempItems = mutableListOf<Value>()
//        tempItems.addAll(pageLoadResult.items)
//        when (pageLoadResult.pageLoadParam.pageLoadType) {
//            PageLoadType.REFRESH_PAGE, PageLoadType.REFRESH_FIRST_PAGE -> {
//            }
//            PageLoadType.PREPEND_DATA -> {
//                reachedTop = pageLoadResult.reachedEnd()
//                if (pageLoadResult.items.size > countInsertableItemIndexes()) {
//                    reachedBottom = false
//                    tempItems.addAll(items.take(pageSize))
//                } else {
//                    tempItems.addAll(items)
//                }
//            }
//            PageLoadType.APPEND_DATA -> {
//                reachedBottom = pageLoadResult.reachedEnd()
//                if (pageLoadResult.items.size > countInsertableItemIndexes()) {
//                    reachedTop = false
//                    tempItems.addAll(0, items.takeLast(pageSize))
//                } else {
//                    tempItems.addAll(0, items)
//                }
//            }
//            PageLoadType.REFRESH_DATA -> {
//                reachedBottom = pageLoadResult.reachedEnd()
//            }
//            PageLoadType.REFRESH_PREPEND_DATA -> {
//                reachedTop = pageLoadResult.reachedEnd()
//                refreshPrependDataLoadKey = null
//            }
//        }
//        items = tempItems.toList()
//        return tempItems
//    }
//
//    private fun countInsertableItemIndexes(): Int {
//        return maxPageSize - items.size
//    }
//
//    private fun getLoadKey(pageLoadType: PageLoadType): Key? {
//        return when (pageLoadType) {
//            PageLoadType.REFRESH_PREPEND_DATA -> refreshPrependDataLoadKey
//            PageLoadType.PREPEND_DATA, PageLoadType.REFRESH_PAGE, PageLoadType.REFRESH_DATA -> items.firstOrNull()?.key
//            PageLoadType.APPEND_DATA -> items.lastOrNull()?.key
//            PageLoadType.REFRESH_FIRST_PAGE -> null
//        }
//    }
//
//    private fun getLoadSize(pageLoadType: PageLoadType): Int {
//        return when (pageLoadType) {
//            PageLoadType.PREPEND_DATA, PageLoadType.APPEND_DATA, PageLoadType.REFRESH_PREPEND_DATA -> pageSize
//            PageLoadType.REFRESH_DATA, PageLoadType.REFRESH_PAGE, PageLoadType.REFRESH_FIRST_PAGE -> max(items.size, pageSize)
//        }
//    }
//
//    private fun getLoadParam(pageLoadType: PageLoadType): PageLoadParam<Key> {
//        if ((pageLoadType != PageLoadType.REFRESH_DATA && items.isEmpty())
//            || (pageLoadType == PageLoadType.REFRESH_PREPEND_DATA && refreshPrependDataLoadKey == null)
//        ) {
//            return PageLoadParam(PageLoadType.REFRESH_DATA, null, pageSize)
//        }
//        return PageLoadParam(pageLoadType, getLoadKey(pageLoadType), getLoadSize(pageLoadType))
//    }



//    override fun loadPage(pageLoadType: PageLoadType) {
//        TODO("Not yet implemented")
//    }

}
