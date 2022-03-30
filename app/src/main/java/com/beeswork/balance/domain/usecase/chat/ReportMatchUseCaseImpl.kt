package com.beeswork.balance.domain.usecase.chat

import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.match.UnmatchDTO
import com.beeswork.balance.internal.constant.ReportReason
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class ReportMatchUseCaseImpl(
    private val matchRepository: MatchRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ReportMatchUseCase {

    override suspend fun invoke(chatId: UUID, swipedId: UUID, reportReason: ReportReason, description: String): Resource<UnmatchDTO> =
        withContext(defaultDispatcher) {
            matchRepository.reportMatch(chatId, swipedId, reportReason, description)
        }

}