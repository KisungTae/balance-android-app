package com.beeswork.balance.ui.common.paging

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.max

class ItemKeyPager<Key : Any, Value : ItemKeyPageable<Key>>(
    private val pageSize: Int,
    private val maxPageSize: Int,
    private val pagingSource: PagingSource<Key, Value>,
    private val coroutineScope: CoroutineScope,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : Pager {


    private var items: List<Value> = arrayListOf()
    private var reachedTop: Boolean = true
    private var reachedBottom: Boolean = true
    private var refreshPrependDataLoadKey: Key? = null

    private var header: Value? = null
    private var footer: Value? = null

    private val _pageUIStateLiveData = MutableLiveData<PageUIState<Value>>()
    private val pageUIStateLiveData = _pageUIStateLiveData

    private val mutex = Mutex()
    private val loadTypeQueue = mutableSetOf<LoadType>()
    private val pageLoadEventChannel = Channel<LoadType>(LOAD_PAGE_CHANNEL_BUFFER)

    val pagingMediator = PagingMediator(this@ItemKeyPager, pageUIStateLiveData)

    init {
        pageLoadEventChannel.consumeAsFlow().onEach { loadType ->
            mutex.withLock {
                loadTypeQueue.remove(loadType)
            }

            if (refreshPrependDataLoadKey != null && loadType != LoadType.REFRESH_PREPEND_DATA) {
                _pageUIStateLiveData.postValue(PageUIState.Error(LoadType.REFRESH_PREPEND_DATA, null))
            }

            val loadResult = loadPage(getLoadParam(loadType))
            if (loadResult is LoadResult.Success
                && (loadResult.loadParam.loadType == LoadType.REFRESH_DATA || loadResult.loadParam.loadType == LoadType.REFRESH_PAGE)
                && loadResult.loadParam.loadKey != null
                && loadResult.items.isNullOrEmpty()
            ) {
                refreshPrependDataLoadKey = loadResult.loadParam.loadKey
                loadPage(getLoadParam(LoadType.REFRESH_PREPEND_DATA))
            }
        }.launchIn(coroutineScope + defaultDispatcher)
    }

    override fun loadPage(loadType: LoadType) {
        coroutineScope.launch {
            mutex.withLock {
                if (!loadTypeQueue.contains(loadType)) {
                    loadTypeQueue.add(loadType)
                    pageLoadEventChannel.send(loadType)
                }
            }
        }
    }

    private suspend fun loadPage(loadParam: LoadParam<Key>): LoadResult<Key, Value> {
        _pageUIStateLiveData.postValue(PageUIState.Loading(loadParam.loadType))
        delay(5000)
        val loadResult = pagingSource.load(loadParam)
        val pageUIState = when (loadResult) {
            is LoadResult.Error -> {
                PageUIState.Error(loadResult.loadType, loadResult.throwable)
            }
            is LoadResult.Success -> {
                val tempItems = mergeLoadResult(loadResult)
                addHeaderAndFooter(tempItems)
                PageUIState.Success(tempItems, loadResult.loadParam.loadType, reachedTop, reachedBottom)
            }
        }
        _pageUIStateLiveData.postValue(pageUIState)
        return loadResult
    }

    private fun addHeaderAndFooter(items: MutableList<Value>) {
        if (reachedTop) {
            header?.let { _header ->
                items.add(0, _header)
            }
        }
        if (reachedBottom) {
            footer?.let { _footer ->
                items.add(_footer)
            }
        }
    }

    private fun mergeLoadResult(loadResult: LoadResult.Success<Key, Value>): MutableList<Value> {
        val tempItems = mutableListOf<Value>()
        tempItems.addAll(loadResult.items)
        when (loadResult.loadParam.loadType) {
            LoadType.REFRESH_PAGE, LoadType.REFRESH_FIRST_PAGE -> {
            }
            LoadType.PREPEND_DATA -> {
                reachedTop = loadResult.reachedEnd()
                if (loadResult.items.size > countInsertableItemIndexes()) {
                    reachedBottom = false
                    tempItems.addAll(items.take(pageSize))
                } else {
                    tempItems.addAll(items)
                }
            }
            LoadType.APPEND_DATA -> {
                reachedBottom = loadResult.reachedEnd()
                if (loadResult.items.size > countInsertableItemIndexes()) {
                    reachedTop = false
                    tempItems.addAll(0, items.takeLast(pageSize))
                } else {
                    tempItems.addAll(0, items)
                }
            }
            LoadType.REFRESH_DATA -> {
                reachedBottom = loadResult.reachedEnd()
            }
            LoadType.REFRESH_PREPEND_DATA -> {
                reachedTop = loadResult.reachedEnd()
                refreshPrependDataLoadKey = null
            }
        }
        items = tempItems.toList()
        return tempItems
    }

    private fun countInsertableItemIndexes(): Int {
        return maxPageSize - items.size
    }

    private fun getLoadKey(loadType: LoadType): Key? {
        return when (loadType) {
            LoadType.REFRESH_PREPEND_DATA -> refreshPrependDataLoadKey
            LoadType.PREPEND_DATA, LoadType.REFRESH_PAGE, LoadType.REFRESH_DATA -> items.firstOrNull()?.key
            LoadType.APPEND_DATA -> items.lastOrNull()?.key
            LoadType.REFRESH_FIRST_PAGE -> null
        }
    }

    private fun getLoadSize(loadType: LoadType): Int {
        return when (loadType) {
            LoadType.PREPEND_DATA, LoadType.APPEND_DATA, LoadType.REFRESH_PREPEND_DATA -> pageSize
            LoadType.REFRESH_DATA, LoadType.REFRESH_PAGE, LoadType.REFRESH_FIRST_PAGE -> max(items.size, pageSize)
        }
    }

    private fun getLoadParam(loadType: LoadType): LoadParam<Key> {
        if ((loadType != LoadType.REFRESH_DATA && items.isEmpty())
            || (loadType == LoadType.REFRESH_PREPEND_DATA && refreshPrependDataLoadKey == null)
        ) {
            return LoadParam(LoadType.REFRESH_DATA, null, pageSize)
        }
        return LoadParam(loadType, getLoadKey(loadType), getLoadSize(loadType))
    }

    fun withHeader(header: Value): ItemKeyPager<Key, Value> {
        this.header = header
        return this
    }

    fun withFooter(footer: Value): ItemKeyPager<Key, Value> {
        this.footer = footer
        return this
    }

    companion object {
        const val LOAD_PAGE_CHANNEL_BUFFER = 10
    }

}
