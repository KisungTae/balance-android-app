package com.beeswork.balance.data.network.rds.card

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.card.CardDTO
import com.beeswork.balance.data.network.response.card.FetchCardsResponse
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.profile.FetchQuestionsDTO
import com.beeswork.balance.internal.constant.ReportReason
import java.util.*

interface CardRDS {
    suspend fun fetchCards(minAge: Int, maxAge: Int, gender: Boolean, distance: Int, pageIndex: Int): Resource<List<CardDTO>>
    suspend fun like(swipedId: UUID): Resource<FetchQuestionsDTO>
    suspend fun reportProfile(reportedId: UUID, reportReason: ReportReason, reportDescription: String?): Resource<EmptyResponse>
}