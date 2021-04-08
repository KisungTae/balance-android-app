package com.beeswork.balance.data.database.repository.match

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PagingSource
import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.database.response.NewChatMessage
import com.beeswork.balance.data.database.response.NewMatch
import com.beeswork.balance.data.database.response.PagingRefresh
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.LoadType
import org.threeten.bp.OffsetDateTime

interface MatchRepository {
    val matchPagingRefreshLiveData: LiveData<PagingRefresh<NewMatch>>
    val chatMessagePagingRefreshLiveData: LiveData<PagingRefresh<NewChatMessage>>

    suspend fun loadMatches(loadSize: Int, startPosition: Int): List<Match>
    suspend fun loadMatches(loadSize: Int, startPosition: Int, searchKeyword: String): List<Match>
    suspend fun fetchMatches(): Resource<EmptyResponse>

    suspend fun sendChatMessage(chatId: Long, body: String)
    suspend fun loadChatMessages(loadSize: Int, startPosition: Int, chatId: Long): List<ChatMessage>
    suspend fun synchronizeMatch(chatId: Long)

    fun testFunction()
}