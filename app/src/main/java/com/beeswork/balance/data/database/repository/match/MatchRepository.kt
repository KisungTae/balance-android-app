package com.beeswork.balance.data.database.repository.match

import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.database.repository.chat.ChatMessageInvalidation
import com.beeswork.balance.data.database.tuple.MatchProfileTuple
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.match.ListMatchesDTO
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.internal.constant.ReportReason
import kotlinx.coroutines.flow.Flow
import java.util.*

interface MatchRepository {

    val newMatchFlow: Flow<MatchProfileTuple>

    suspend fun loadMatches(loadSize: Int, startPosition: Int): List<Match>
    suspend fun loadMatches(loadSize: Int, startPosition: Int, searchKeyword: String): List<Match>
    suspend fun fetchMatches(): Resource<ListMatchesDTO>
    suspend fun synchronizeMatch(chatId: Long)
    suspend fun isUnmatched(chatId: Long): Boolean
    suspend fun unmatch(chatId: Long, swipedId: UUID): Resource<EmptyResponse>
    suspend fun reportMatch(
        chatId: Long,
        swipedId: UUID,
        reportReason: ReportReason,
        description: String
    ): Resource<EmptyResponse>
    suspend fun saveMatch(matchDTO: MatchDTO)
    suspend fun getMatchInvalidation(): Flow<Boolean>
    suspend fun getUnreadMatchCount(): Flow<Int>

    fun testFunction()
}