package com.beeswork.balance.ui.common.paging

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow

class ItemKeyPager<Key : Any, Value : ItemKeyPageable<Key>>(
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

    private var itemKeyPage: ItemKeyPage<Key, Value> = ItemKeyPage(pageSize, numOfPagesToKeep)

    val pagingMediator = PagingMediator(pageLoadEventChannel, pageSnapshotLiveData)

    init {
        viewModelScope.launch(defaultDispatcher) {
            pageLoadEventChannel.consumeAsFlow().collect { loadType ->
                // loadType == REFRESH_PAGE but items.size == 0 then no need to refresh


//                when (val loadResult = pagingSource.load(page.getLoadKey(loadType), loadType, page.getLoadSize(loadType))) {
//                    is LoadResult.Success -> {
//                        val pageSnapshot = page.insertAndGeneratePageSnapshot(loadResult)
//                        _pageSnapshotLiveData.postValue(pageSnapshot)
//                    }
//                    is LoadResult.Error -> {
//                        _pageSnapshotLiveData.postValue(PageSnapshot.Error(loadResult.loadType, loadResult.throwable))
//                    }
//                }
            }
        }
    }


    companion object {
        const val LOAD_PAGE_CHANNEL_BUFFER = 10
    }

}