package com.beeswork.balance.data.network.rds.match

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.match.ListMatchesDTO
import org.threeten.bp.OffsetDateTime
import java.util.*


interface MatchRDS {

    suspend fun unmatch(
        accountId: UUID?,
        identityToken: UUID?,
        unmatchedId: UUID
    ): Resource<EmptyResponse>

    suspend fun listMatches(
        accountId: UUID?,
        identityToken: UUID?,
        matchFetchedAt: OffsetDateTime
    ): Resource<ListMatchesDTO>
}