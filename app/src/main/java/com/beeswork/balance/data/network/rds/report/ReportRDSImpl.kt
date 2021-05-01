package com.beeswork.balance.data.network.rds.report

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.rds.BaseRDS
import com.beeswork.balance.data.network.request.ReportBody
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.ReportReason

import java.util.*

class ReportRDSImpl(
    private val balanceAPI: BalanceAPI
) : BaseRDS(), ReportRDS {
    override suspend fun reportProfile(
        accountId: UUID?,
        identityToken: UUID?,
        reportedId: UUID?,
        reportReason: ReportReason,
        description: String
    ): Resource<EmptyResponse> {
        return getResult {
            balanceAPI.reportProfile(
                ReportBody(
                    accountId,
                    identityToken,
                    reportedId,
                    reportReason,
                    description
                )
            )
        }
    }

    override suspend fun reportMatch(
        accountId: UUID?,
        identityToken: UUID?,
        reportedId: UUID?,
        reportReason: ReportReason,
        description: String
    ): Resource<EmptyResponse> {
        return getResult {
            balanceAPI.reportMatch(
                ReportBody(
                    accountId,
                    identityToken,
                    reportedId,
                    reportReason,
                    description
                )
            )
        }
    }
}