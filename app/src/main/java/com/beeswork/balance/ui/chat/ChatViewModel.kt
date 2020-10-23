package com.beeswork.balance.ui.chat

import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.internal.constant.CHAT_MAX_PAGE_SIZE
import com.beeswork.balance.internal.constant.CHAT_PAGE_PREFETCH_DISTANCE
import com.beeswork.balance.internal.constant.CHAT_PAGE_SIZE
import com.beeswork.balance.internal.lazyDeferred

class ChatViewModel(
    private val chatId: Long,
    private val balanceRepository: BalanceRepository
) : ViewModel() {

    private val pagedListConfig = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setMaxSize(CHAT_MAX_PAGE_SIZE)
        .setInitialLoadSizeHint(CHAT_PAGE_SIZE)
        .setPageSize(CHAT_PAGE_SIZE)
        .setPrefetchDistance(CHAT_PAGE_PREFETCH_DISTANCE)
        .build()


    val messages by lazyDeferred {
        LivePagedListBuilder(balanceRepository.getMessages(chatId), pagedListConfig).build()
    }
}