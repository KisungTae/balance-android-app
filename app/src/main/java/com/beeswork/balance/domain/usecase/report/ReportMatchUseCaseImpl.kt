package com.beeswork.balance.domain.usecase.report

import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.match.UnmatchDTO
import com.beeswork.balance.internal.constant.ReportReason
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

class ReportMatchUseCaseImpl(
    private val matchRepository: MatchRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
): ReportMatchUseCase {

    override suspend fun invoke(reportedId: UUID, reportReason: ReportReason, reportDescription: String?): Resource<EmptyResponse> {
        return try {
            withContext(defaultDispatcher) {
                return@withContext matchRepository.reportMatch(reportedId, reportReason, reportDescription)
            }
        } catch (e: IOException) {
            return Resource.error(e)
        }
    }
}