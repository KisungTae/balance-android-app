package com.beeswork.balance.data.network.rds.match

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.click.ClickBody
import com.beeswork.balance.data.network.request.match.UnmatchBody
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.match.ListMatchesDTO
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import org.threeten.bp.OffsetDateTime
import java.util.*


class MatchRDSImpl(
    balanceAPI: BalanceAPI,
    preferenceProvider: PreferenceProvider
) : BaseRDS(balanceAPI, preferenceProvider), MatchRDS {

    override suspend fun click(accountId: UUID, swipedId: UUID, answers: Map<Int, Boolean>): Resource<MatchDTO> {
        return getResult { balanceAPI.click(ClickBody(accountId, swipedId, answers)) }
    }

    override suspend fun unmatch(accountId: UUID, swipedId: UUID): Resource<EmptyResponse> {
        return getResult { balanceAPI.unmatch(UnmatchBody(accountId, swipedId)) }
    }

    override suspend fun listMatches(accountId: UUID, fetchedAt: OffsetDateTime): Resource<ListMatchesDTO> {
        return getResult { balanceAPI.listMatches(accountId, fetchedAt) }
    }
}