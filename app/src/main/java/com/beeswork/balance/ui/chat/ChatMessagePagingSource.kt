package com.beeswork.balance.ui.chat

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.database.repository.chat.ChatRepository

class ChatMessagePagingSource(
    private val chatRepository: ChatRepository,
    private val chatId: Long
): PagingSource<Int, ChatMessage>() {

    override fun getRefreshKey(state: PagingState<Int, ChatMessage>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ChatMessage> {
        val currentPage = params.key ?: 0
        val matches = chatRepository.loadChatMessages(params.loadSize, (currentPage * params.loadSize), chatId)
        val prevPage = if (currentPage >= 1) currentPage - 1 else null
        val nextPage = if (matches.isEmpty()) null else currentPage + 1
        return LoadResult.Page(matches, prevPage, nextPage)
    }
}