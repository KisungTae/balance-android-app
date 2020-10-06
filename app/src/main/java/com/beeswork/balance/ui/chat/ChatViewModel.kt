package com.beeswork.balance.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.beeswork.balance.data.entity.Message
import com.beeswork.balance.data.repository.BalanceRepository
import com.beeswork.balance.internal.lazyDeferred

const val PAGE_SIZE = 30

class ChatViewModel(
    private val matchId: Int,
    private val balanceRepository: BalanceRepository
): ViewModel() {

    private val pagedListConfig = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setInitialLoadSizeHint(PAGE_SIZE)
        .setPageSize(PAGE_SIZE)
        .build()

    val messages by lazyDeferred {
        LivePagedListBuilder(balanceRepository.getMessages(matchId), pagedListConfig).build()
    }
}