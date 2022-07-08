package com.beeswork.balance.ui.common.paging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow

class Pager<Key : Any, Value : Any>(
    private val pageSize: Int,
    private val numOfPagesToKeep: Int,
    private val pagingSource: PagingSource<Key, Value>,
    private val viewModelScope: CoroutineScope,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    private val _pageSnapshotLiveData = MutableLiveData<PageSnapshot<Value>>()
    private val pageSnapshotLiveData = _pageSnapshotLiveData

    // channel
    private val pageLoadEventChannel = Channel<LoadType>(LOAD_PAGE_CHANNEL_BUFFER)

    private var page: Page<Key, Value> = Page(pageSize, numOfPagesToKeep)

    val pagingMediator = PagingMediator(pageLoadEventChannel, pageSnapshotLiveData)

    init {
        viewModelScope.launch(defaultDispatcher) {
            pageLoadEventChannel.consumeAsFlow().collect { loadType ->
                val loadResult = pagingSource.load(page.getLoadKey(loadType), loadType, page.getLoadSize(loadType))
                val pageSnapshot = page.insertAndGeneratePageSnapshot(loadResult)
                _pageSnapshotLiveData.postValue(pageSnapshot)
            }
        }
    }


    companion object {
        const val LOAD_PAGE_CHANNEL_BUFFER = 10
    }


    class PagingMediator<Value : Any>(
        val pageLoadEventChannel: Channel<LoadType>,
        val pageSnapshotLiveData: LiveData<PageSnapshot<Value>>
    )

}