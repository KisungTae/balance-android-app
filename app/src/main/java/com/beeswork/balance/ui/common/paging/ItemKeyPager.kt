package com.beeswork.balance.ui.common.paging

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ItemKeyPager<Key : Any, Value : ItemKeyPageable<Key>>(
    private val pageSize: Int,
    private val maxPageSize: Int,
    private val pagingSource: PagingSource<Key, Value>,
    private val viewModelScope: CoroutineScope,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : Pager {

    private val _pageUIStateLiveData = MutableLiveData<PageUIState<Value>>()
    private val pageUIStateLiveData = _pageUIStateLiveData

    private val mutex = Mutex()
    private val loadTypeQueue = mutableSetOf<LoadType>()
    private val pageLoadEventChannel = Channel<LoadType>(LOAD_PAGE_CHANNEL_BUFFER)
    private var itemKeyPage: ItemKeyPage<Key, Value> = ItemKeyPage(pageSize, maxPageSize)
    val pagingMediator = PagingMediator(this@ItemKeyPager, pageUIStateLiveData)


    init {
        pageLoadEventChannel.consumeAsFlow().onEach { loadType ->
            mutex.withLock {
                loadTypeQueue.remove(loadType)
            }

            if (itemKeyPage.shouldClearRefreshPrependDataError() && loadType != LoadType.PREPEND_DATA_AFTER_EMPTY_REFRESH) {
                _pageUIStateLiveData.postValue(PageUIState.Error(LoadType.PREPEND_DATA_AFTER_EMPTY_REFRESH, null))
            }

            val loadResult = doLoadPage(itemKeyPage.getLoadParam(loadType))
            if (loadResult is LoadResult.Success
                && loadResult.loadParam.loadType.isRefresh()
                && loadResult.loadParam.loadKey != null
                && loadResult.items.isNullOrEmpty()) {
                doLoadPage(itemKeyPage.getLoadParam(LoadType.PREPEND_DATA_AFTER_EMPTY_REFRESH))
            }
        }.launchIn(viewModelScope + defaultDispatcher)
    }

    private suspend fun doLoadPage(loadParam: LoadParam<Key>): LoadResult<Key, Value> {
        _pageUIStateLiveData.postValue(PageUIState.Loading(loadParam.loadType))
        val loadResult = pagingSource.load(loadParam)
        val pageUIState = itemKeyPage.mergeLoadResult(loadResult)
        _pageUIStateLiveData.postValue(pageUIState)
        return loadResult
    }

    override suspend fun loadPage(loadType: LoadType) {
        mutex.withLock {
            if (!loadTypeQueue.contains(loadType)) {
                loadTypeQueue.add(loadType)
                pageLoadEventChannel.send(loadType)
            }
        }
    }


    companion object {
        const val LOAD_PAGE_CHANNEL_BUFFER = 10
    }

}


//        viewModelScope.launch(defaultDispatcher) {
//            pageLoadEventChannel.consumeAsFlow().collect { loadType ->
//                if (refreshFailedLoadKey != null) {
//                    when (val loadResult = pagingSource.load(refreshFailedLoadKey, LoadType.PREPEND_DATA, pageSize)) {
//                        is LoadResult.Error -> {
//                            _pageUIStateLiveData.postValue(PageUIState.Error(LoadType.REFRESH_DATA, loadResult.throwable))
//                            return@collect
//                        }
//                        is LoadResult.Success -> {
//                            if (loadType == LoadType.REFRESH_DATA) {
//                                val pageSnapshot = itemKeyPage.insertAndGeneratePageSnapshot(loadResult)
//                                refreshFailedLoadKey = null
//                                _pageUIStateLiveData.postValue(pageSnapshot)
//                                return@collect
//                            }
//                        }
//                    }
//                }
//
//
//                if (itemKeyPage.isEmpty()) {
//                    when (val loadResult = pagingSource.load(null, LoadType.REFRESH_DATA, pageSize)) {
//                        is LoadResult.Error -> {
//                            _pageUIStateLiveData.postValue(PageUIState.Error(LoadType.REFRESH_DATA, loadResult.throwable))
//                        }
//                        is LoadResult.Success -> {
//                            val pageSnapshot = itemKeyPage.insertAndGeneratePageSnapshot(loadResult)
//                            refreshFailedLoadKey = null
//                            _pageUIStateLiveData.postValue(pageSnapshot)
//                        }
//                    }
//                } else {
//                    // should care when list is empty and refresh_data, so the laod_key is null
//
//                    val loadKey = itemKeyPage.getLoadKey(loadType)
//                    when (val loadResult = pagingSource.load(loadKey, loadType, itemKeyPage.getLoadSize(loadType))) {
//                        is LoadResult.Success -> {
//                            if (loadResult.items.isNullOrEmpty() && loadResult.loadType == LoadType.REFRESH_DATA && loadKey != null) {
//                                when (val prependLoadResult = pagingSource.load(loadKey, LoadType.PREPEND_DATA, pageSize)) {
//                                    is LoadResult.Error -> {
//                                        refreshFailedLoadKey = loadKey
//                                        _pageUIStateLiveData.postValue(
//                                            PageUIState.Error(
//                                                LoadType.REFRESH_DATA,
//                                                prependLoadResult.throwable
//                                            )
//                                        )
//                                    }
//                                    is LoadResult.Success -> {
//                                        val pageSnapshot = itemKeyPage.insertAndGeneratePageSnapshot(prependLoadResult)
//                                        refreshFailedLoadKey = null
//                                        _pageUIStateLiveData.postValue(pageSnapshot)
//                                    }
//                                }
//                                return@collect
//                            }
//
//
//                            val pageSnapshot = itemKeyPage.insertAndGeneratePageSnapshot(loadResult)
//                            _pageUIStateLiveData.postValue(pageSnapshot)
//                        }
//                        is LoadResult.Error -> {
//                            _pageUIStateLiveData.postValue(PageUIState.Error(loadResult.loadType, loadResult.throwable))
//                        }
//                    }
//                }
//
//
//                // check failed refresh job
//
//                // if initial_load then just do
//
//                // if page empty then refresh_data
//
//                // if done refreshfailed job and if loadtype == REFERS_DATA then no need to do that
//
//                // when referesh_Data error then no need to prepend or append, does not matter but make sure prepend or append after refre_data does not hide refresh_data
//
//                // when error, then ignore requests from then
//
//                // loadType == REFRESH_PAGE but items.size == 0 then no need to refresh
//
//                // when loadType == REFRESH_DATA and empty fetched then prepend with load key
//
//                // when PREPEND_NEW but empty list then APPEND
//
//                // when prepend but key null which means empty list , then it should be refresh_data?
//
//
//            }
//        }