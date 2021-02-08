package com.beeswork.balance.data.network.rds.match

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.match.ListMatchResponse


interface MatchRDS {
    suspend fun listMatches(
        accountId: String,
        identityToken: String,
        lastAccountUpdatedAt: String,
        lastMatchUpdatedAt: String,
        lastChatMessageCreatedAt: String
    ): Resource<ListMatchResponse>
}