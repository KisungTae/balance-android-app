package com.beeswork.balance.data.database.repository.match

import com.beeswork.balance.data.database.entity.match.Match
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.match.ClickDTO
import com.beeswork.balance.data.network.response.match.ListMatchesDTO
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.internal.constant.MatchPageFilter
import com.beeswork.balance.internal.constant.ReportReason
import com.beeswork.balance.ui.mainviewpagerfragment.NewMatch
import kotlinx.coroutines.flow.Flow
import java.util.*

interface MatchRepository {

    val newMatchFlow: Flow<NewMatch>

    suspend fun loadMatches(loadSize: Int, startPosition: Int, matchPageFilter: MatchPageFilter?): List<Match>
    suspend fun fetchMatches(loadSize: Int, lastSwipedId: UUID?, matchPageFilter: MatchPageFilter?): Resource<ListMatchesDTO>
    suspend fun unmatch(chatId: UUID, swipedId: UUID): Resource<EmptyResponse>
    suspend fun reportMatch(
        chatId: UUID,
        swipedId: UUID,
        reportReason: ReportReason,
        description: String
    ): Resource<EmptyResponse>
    suspend fun saveMatch(matchDTO: MatchDTO)
    fun getMatchPageInvalidationFlow(): Flow<Boolean>
    fun getMatchCountFlow(): Flow<Long?>
    fun getMatchFlow(chatId: UUID): Flow<Match?>
    suspend fun click(swipedId: UUID, answers: Map<Int, Boolean>): Resource<ClickDTO>
    suspend fun deleteMatches()
    suspend fun deleteMatchCount()
    suspend fun isUnmatched(chatId: UUID): Boolean

    fun testFunction()
}