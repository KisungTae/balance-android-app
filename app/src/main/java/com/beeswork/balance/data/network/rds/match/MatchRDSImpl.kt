package com.beeswork.balance.data.network.rds.match

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.click.ClickBody
import com.beeswork.balance.data.network.request.match.UnmatchBody
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.match.ClickDTO
import com.beeswork.balance.data.network.response.match.ListMatchesDTO
import com.beeswork.balance.internal.constant.MatchPageFilter
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import java.util.*


class MatchRDSImpl(
    balanceAPI: BalanceAPI,
    preferenceProvider: PreferenceProvider
) : BaseRDS(balanceAPI, preferenceProvider), MatchRDS {

    override suspend fun click(swipedId: UUID, answers: Map<Int, Boolean>): Resource<ClickDTO> {
        return getResult { balanceAPI.click(ClickBody(swipedId, answers)) }
    }

    override suspend fun unmatch(swipedId: UUID): Resource<EmptyResponse> {
        return getResult { balanceAPI.unmatch(UnmatchBody(swipedId)) }
    }

    override suspend fun fetchMatches(loadSize: Int, lastSwipedId: UUID?, matchPageFilter: MatchPageFilter?): Resource<ListMatchesDTO> {
        return getResult { balanceAPI.fetchMatches(loadSize, lastSwipedId, matchPageFilter) }
    }

    override suspend fun listMatches(loadSize: Int, startPosition: Int, matchPageFilter: MatchPageFilter?): Resource<ListMatchesDTO> {
        return getResult { balanceAPI.listMatches(loadSize, startPosition, matchPageFilter) }
    }
}