package com.beeswork.balance.ui.common.paging

import androidx.lifecycle.LiveData
import kotlinx.coroutines.channels.Channel

class PagingMediator<Value : Any>(
    val pageLoadEventChannel: Channel<LoadType>,
    val pageSnapshotLiveData: LiveData<PageSnapshot<Value>>
)