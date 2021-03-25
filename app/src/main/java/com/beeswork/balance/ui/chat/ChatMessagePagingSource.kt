package com.beeswork.balance.ui.chat

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.database.repository.chat.ChatRepository

class ChatMessagePagingSource(
    private val chatRepository: ChatRepository,
    private val searchKeyword: String,
    private val lastSearchedChatMessageKey: Long?
): PagingSource<Long, ChatMessage>() {
    override fun getRefreshKey(state: PagingState<Long, ChatMessage>): Long? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, ChatMessage> {


        TODO("Not yet implemented")
    }

}