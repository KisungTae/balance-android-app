package com.beeswork.balance.data.listener

import com.beeswork.balance.data.database.response.ChatMessagePagingRefresh

interface ChatMessagePagingRefreshListener {
    fun onRefresh(chatMessagePagingRefresh: ChatMessagePagingRefresh)
}