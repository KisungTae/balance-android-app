package com.beeswork.balance.data.network.rds.match

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.match.ListMatchResponse
import org.threeten.bp.OffsetDateTime
import java.util.*


interface MatchRDS {
    suspend fun listMatches(
        accountId: UUID,
        identityToken: UUID,
        lastAccountUpdatedAt: OffsetDateTime,
        lastMatchUpdatedAt: OffsetDateTime,
        lastChatMessageCreatedAt: OffsetDateTime
    ): Resource<ListMatchResponse>
}