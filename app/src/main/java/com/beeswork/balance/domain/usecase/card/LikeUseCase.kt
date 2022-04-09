package com.beeswork.balance.domain.usecase.card

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.profile.FetchQuestionsDTO
import java.util.*

interface LikeUseCase {

    suspend fun invoke(swipedId: UUID): Resource<FetchQuestionsDTO>
}