package com.beeswork.balance.data.network.rds.card

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.common.ReportBody
import com.beeswork.balance.data.network.request.swipe.LikeBody
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.card.CardDTO
import com.beeswork.balance.data.network.response.card.FetchCardsResponse
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.profile.FetchQuestionsDTO
import com.beeswork.balance.internal.constant.ReportReason
import java.util.*

class CardRDSImpl(
    private  val balanceAPI: BalanceAPI
) : BaseRDS(), CardRDS {

    override suspend fun fetchCards(
        minAge: Int,
        maxAge: Int,
        gender: Boolean,
        distance: Int,
        pageIndex: Int
    ): Resource<List<CardDTO>> {
        return getResult { balanceAPI.recommend(minAge, maxAge, gender, distance, pageIndex) }
    }

    override suspend fun like(swipedId: UUID): Resource<FetchQuestionsDTO> {
        return getResult { balanceAPI.like(LikeBody(swipedId)) }
    }

    override suspend fun reportProfile(
        reportedId: UUID,
        reportReason: ReportReason,
        reportDescription: String?
    ): Resource<EmptyResponse> {
        return getResult { balanceAPI.reportProfile(ReportBody(reportedId, reportReason, reportDescription)) }
    }
}