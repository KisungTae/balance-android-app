package com.beeswork.balance.ui.chat

import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.internal.lazyDeferred

const val CHAT_PAGE_SIZE = 30
const val CHAT_PAGE_PREFETCH_DISTANCE = CHAT_PAGE_SIZE * 2
const val CHAT_MAX_PAGE_SIZE = CHAT_PAGE_PREFETCH_DISTANCE * 2 + CHAT_PAGE_SIZE

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