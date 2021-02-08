package com.beeswork.balance.data.network.rds.match

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.match.ListMatchResponse


class MatchRDSImpl(
    private val balanceAPI: BalanceAPI
) : BaseRDS(), MatchRDS {
    override suspend fun listMatches(
        accountId: String,
        identityToken: String,
        lastAccountUpdatedAt: String,
        lastMatchUpdatedAt: String,
        lastChatMessageCreatedAt: String
    ): Resource<ListMatchResponse> {
        return getResult {
            balanceAPI.listMatches(
                accountId,
                identityToken,
                lastAccountUpdatedAt,
                lastMatchUpdatedAt,
                lastChatMessageCreatedAt
            )
        }
    }
}