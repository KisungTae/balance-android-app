package com.beeswork.balance.data.network.rds.report

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.match.UnmatchDTO
import com.beeswork.balance.internal.constant.ReportReason
import java.util.*

interface ReportRDS {
    suspend fun reportProfile(reportedId: UUID, reportReason: ReportReason, description: String): Resource<EmptyResponse>
    suspend fun reportMatch(reportedId: UUID, reportReason: ReportReason, description: String): Resource<UnmatchDTO>
}