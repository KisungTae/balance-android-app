package com.beeswork.balance.domain.usecase.report

import com.beeswork.balance.data.database.repository.card.CardRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.ReportReason
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

class ReportProfileUseCaseImpl(
    private val cardRepository: CardRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
): ReportProfileUseCase {

    override suspend fun invoke(reportedId: UUID, reportReason: ReportReason, reportDescription: String?): Resource<EmptyResponse> {
        return try {
            withContext(defaultDispatcher) {
                delay(3000)
                throw IOException("test exception fdfas fdsf dsfdsaf dasfds fdsafdsafdsf dsfdsaf sdfd fdsaf sadfdsaf dsafdsfa dsf dsfdsafds fsd fdsfasfsda fdsafdsafsadfds afdsafsdfdsaf dfdsafdsafas dfdsafdasfdas fadsfdsafdsaf")
//                return@withContext cardRepository.reportProfile(reportedId, reportReason, reportDescription)
            }
        } catch (e: IOException) {
            return Resource.error(e)
        }
    }
}