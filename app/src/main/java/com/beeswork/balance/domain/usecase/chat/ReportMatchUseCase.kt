package com.beeswork.balance.domain.usecase.chat

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.match.UnmatchDTO
import com.beeswork.balance.internal.constant.ReportReason
import java.util.*

interface ReportMatchUseCase {
    suspend fun invoke(chatId: UUID, swipedId: UUID, reportReason: ReportReason, description: String): Resource<UnmatchDTO>
}