package com.beeswork.balance.data.network.request.common

import com.beeswork.balance.internal.constant.ReportReason
import java.util.*

data class ReportBody(
    val reportedId: UUID,
    val reportReasonId: ReportReason,
    val reportDescription: String?
)