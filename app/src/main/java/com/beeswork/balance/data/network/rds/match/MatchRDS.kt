package com.beeswork.balance.data.network.rds.match

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.match.ClickDTO
import com.beeswork.balance.data.network.response.match.ListMatchesDTO
import com.beeswork.balance.data.network.response.match.UnmatchDTO
import com.beeswork.balance.internal.constant.MatchPageFilter
import java.util.*


interface MatchRDS {
    suspend fun click(swipedId: UUID, answers: Map<Int, Boolean>): Resource<ClickDTO>
    suspend fun unmatch(swipedId: UUID): Resource<UnmatchDTO>
    suspend fun fetchMatches(loadSize: Int, lastMatchId: Long?, matchPageFilter: MatchPageFilter?): Resource<ListMatchesDTO>
    suspend fun listMatches(loadSize: Int, startPosition: Int, matchPageFilter: MatchPageFilter?): Resource<ListMatchesDTO>
    suspend fun syncMatch(chatId: UUID, lastReadReceivedChatMessageId: Long): Resource<EmptyResponse>
}