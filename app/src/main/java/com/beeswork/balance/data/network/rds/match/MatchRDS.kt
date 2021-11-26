package com.beeswork.balance.data.network.rds.match

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.match.ListMatchesDTO
import com.beeswork.balance.data.network.response.match.MatchDTO
import org.threeten.bp.OffsetDateTime
import java.util.*


interface MatchRDS {
    suspend fun click(swipedId: UUID, answers: Map<Int, Boolean>): Resource<MatchDTO>
    suspend fun unmatch(swipedId: UUID): Resource<EmptyResponse>
    suspend fun listMatches(fetchedAt: OffsetDateTime): Resource<ListMatchesDTO>
}