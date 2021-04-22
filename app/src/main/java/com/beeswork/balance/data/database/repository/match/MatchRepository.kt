package com.beeswork.balance.data.database.repository.match

import androidx.lifecycle.LiveData
import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.database.response.PagingRefresh
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.database.response.NewChatMessage
import com.beeswork.balance.data.database.response.NewMatch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*

interface MatchRepository {

    val chatMessagePagingRefreshFlow: Flow<PagingRefresh<NewChatMessage>>
    val matchPagingRefreshFlow: Flow<PagingRefresh<NewMatch>>
    val sendChatMessageFlow: Flow<Resource<EmptyResponse>>

    fun collectStompClientFlows()

    suspend fun loadMatches(loadSize: Int, startPosition: Int): List<Match>
    suspend fun loadMatches(loadSize: Int, startPosition: Int, searchKeyword: String): List<Match>
    suspend fun fetchMatches(): Resource<EmptyResponse>
    suspend fun synchronizeMatch(chatId: Long)
    suspend fun isUnmatched(chatId: Long): Boolean

    suspend fun sendChatMessage(chatId: Long, matchedId: UUID, body: String)
    suspend fun loadChatMessages(loadSize: Int, startPosition: Int, chatId: Long): List<ChatMessage>


    fun testFunction()
}