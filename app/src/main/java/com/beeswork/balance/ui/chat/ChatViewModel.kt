package com.beeswork.balance.ui.chat

import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.beeswork.balance.data.repository.BalanceRepository
import com.beeswork.balance.internal.constant.CHAT_PAGE_SIZE
import com.beeswork.balance.internal.lazyDeferred

class ChatViewModel(
    private val chatId: Long,
    private val balanceRepository: BalanceRepository
): ViewModel() {

    private val pagedListConfig = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setInitialLoadSizeHint(CHAT_PAGE_SIZE)
        .setPageSize(CHAT_PAGE_SIZE)
        .build()

    val messages by lazyDeferred {
        LivePagedListBuilder(balanceRepository.getMessages(chatId), pagedListConfig).build()
    }
}