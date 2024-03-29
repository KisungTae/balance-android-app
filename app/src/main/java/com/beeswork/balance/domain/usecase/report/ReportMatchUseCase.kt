package com.beeswork.balance.domain.usecase.report

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.match.UnmatchDTO
import com.beeswork.balance.internal.constant.ReportReason
import java.util.*

interface ReportMatchUseCase {

    suspend fun invoke(reportedId: UUID, reportReason: ReportReason, reportDescription: String?): Resource<EmptyResponse>
}