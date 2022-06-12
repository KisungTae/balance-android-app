package com.beeswork.balance.data.database.repository.match

import com.beeswork.balance.data.database.entity.match.Match
import com.beeswork.balance.data.database.result.ClickResult
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.match.*
import com.beeswork.balance.internal.constant.MatchPageFilter
import com.beeswork.balance.internal.constant.ReportReason
import kotlinx.coroutines.flow.Flow
import java.util.*

interface MatchRepository {

    val newMatchFlow: Flow<Match>

    suspend fun loadMatches(loadSize: Int, startPosition: Int, matchPageFilter: MatchPageFilter?, sync: Boolean): List<Match>
    suspend fun fetchMatches(loadSize: Int, lastMatchId: Long?, matchPageFilter: MatchPageFilter?): Resource<ListMatchesDTO>
    suspend fun unmatch(chatId: UUID, swipedId: UUID): Resource<UnmatchDTO>
    suspend fun reportMatch(swipedId: UUID, reportReason: ReportReason, reportDescription: String?): Resource<EmptyResponse>
    suspend fun saveMatch(matchDTO: MatchDTO)
    fun getMatchPageInvalidationFlow(): Flow<Boolean>
    fun getMatchFlow(chatId: UUID): Flow<Match?>
    suspend fun click(swipedId: UUID, answers: Map<Int, Boolean>): Resource<ClickResult>
    suspend fun deleteMatches()
    suspend fun isUnmatched(chatId: UUID): Boolean
    fun syncMatch(chatId: UUID)
    fun syncMatches(loadSize: Int, startPosition: Int?, matchPageFilter: MatchPageFilter?)

    fun testFunction()
}