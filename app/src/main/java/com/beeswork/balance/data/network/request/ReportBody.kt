package com.beeswork.balance.data.network.request

import com.beeswork.balance.internal.constant.ReportReason
import java.util.*

data class ReportBody(
    val accountId: UUID,
    val reportedId: UUID,
    val reportReasonId: ReportReason,
    val description: String
)