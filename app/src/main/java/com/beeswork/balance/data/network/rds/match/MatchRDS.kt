package com.beeswork.balance.data.network.rds.match

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.match.ListMatchesDTO
import org.threeten.bp.OffsetDateTime
import java.util.*


interface MatchRDS {
    suspend fun listMatches(
        accountId: UUID,
        identityToken: UUID,
        matchFetchedAt: OffsetDateTime,
        accountFetchedAt: OffsetDateTime,
        chatMessageFetchedAt: OffsetDateTime
    ): Resource<ListMatchesDTO>
}