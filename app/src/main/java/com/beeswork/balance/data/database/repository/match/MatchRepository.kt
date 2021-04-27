package com.beeswork.balance.data.database.repository.match

import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.database.response.ChatMessagePagingRefresh
import com.beeswork.balance.data.database.response.MatchPagingRefresh
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.database.response.NewChatMessage
import com.beeswork.balance.data.database.response.NewMatch
import kotlinx.coroutines.flow.Flow
import java.util.*

interface MatchRepository {

    val chatMessagePagingRefreshFlow: Flow<ChatMessagePagingRefresh>
    val matchPagingRefreshFlow: Flow<MatchPagingRefresh>
    val sendChatMessageFlow: Flow<Resource<EmptyResponse>>

    suspend fun loadMatches(loadSize: Int, startPosition: Int): List<Match>
    suspend fun loadMatches(loadSize: Int, startPosition: Int, searchKeyword: String): List<Match>
    suspend fun fetchMatches(): Resource<EmptyResponse>
    suspend fun synchronizeMatch(chatId: Long)
    suspend fun isUnmatched(chatId: Long): Boolean

    suspend fun sendChatMessage(chatId: Long, matchedId: UUID, body: String)
    suspend fun loadChatMessages(loadSize: Int, startPosition: Int, chatId: Long): List<ChatMessage>
    suspend fun resendChatMessage(key: Long, matchedId: UUID)
    suspend fun deleteChatMessage(key: Long)

    fun testFunction()
}