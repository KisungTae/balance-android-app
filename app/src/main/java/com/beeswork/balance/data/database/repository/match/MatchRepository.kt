package com.beeswork.balance.data.database.repository.match

import com.beeswork.balance.data.database.entity.match.Match
import com.beeswork.balance.data.database.entity.match.MatchProfileTuple
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.internal.constant.ClickResult
import com.beeswork.balance.internal.constant.PushType
import com.beeswork.balance.internal.constant.ReportReason
import kotlinx.coroutines.flow.Flow
import java.util.*

interface MatchRepository {

    val newMatchFlow: Flow<Match>

    suspend fun loadMatches(loadSize: Int, startPosition: Int): List<Match>
    suspend fun loadMatches(loadSize: Int, startPosition: Int, searchKeyword: String): List<Match>
    suspend fun fetchMatches(): Resource<EmptyResponse>
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
    fun getMatchPageInvalidationFlow(): Flow<Boolean>
    fun getMatchCountFlow(): Flow<Long?>
    suspend fun click(swipedId: UUID, answers: Map<Int, Boolean>): Resource<ClickResult>
    suspend fun deleteMatches()
    suspend fun deleteMatchCount()

    fun testFunction()
}