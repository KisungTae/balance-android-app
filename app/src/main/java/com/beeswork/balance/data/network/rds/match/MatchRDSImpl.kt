package com.beeswork.balance.data.network.rds.match

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.UnmatchBody
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.match.ListMatchesDTO
import org.threeten.bp.OffsetDateTime
import java.util.*


class MatchRDSImpl(
    private val balanceAPI: BalanceAPI
) : BaseRDS(), MatchRDS {
    override suspend fun unmatch(accountId: UUID?, identityToken: UUID?, unmatchedId: UUID): Resource<EmptyResponse> {
        return getResult { balanceAPI.unmatch(UnmatchBody(accountId, identityToken, unmatchedId)) }
    }

    override suspend fun listMatches(
        accountId: UUID?,
        identityToken: UUID?,
        matchFetchedAt: OffsetDateTime
    ): Resource<ListMatchesDTO> {
        return getResult {
            balanceAPI.listMatches(
                accountId,
                identityToken,
                matchFetchedAt,
            )
        }
    }
}