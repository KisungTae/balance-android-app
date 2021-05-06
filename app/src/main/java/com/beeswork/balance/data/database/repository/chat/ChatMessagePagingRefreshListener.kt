package com.beeswork.balance.data.database.repository.chat

interface ChatMessagePagingRefreshListener {
    fun onRefresh(chatMessagePagingRefresh: ChatMessagePagingRefresh)
}