package com.beeswork.balance.data.network.rds.report

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.common.ReportBody
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.ReportReason
import com.beeswork.balance.internal.provider.preference.PreferenceProvider

import java.util.*

class ReportRDSImpl(
    private val balanceAPI: BalanceAPI
) : BaseRDS(), ReportRDS {
    override suspend fun reportProfile(
        reportedId: UUID,
        reportReason: ReportReason,
        description: String
    ): Resource<EmptyResponse> {
        return getResult { balanceAPI.reportProfile(ReportBody(reportedId, reportReason, description)) }
    }

    override suspend fun reportMatch(
        reportedId: UUID,
        reportReason: ReportReason,
        description: String
    ): Resource<EmptyResponse> {
        return getResult { balanceAPI.reportMatch(ReportBody(reportedId, reportReason, description)) }
    }
}