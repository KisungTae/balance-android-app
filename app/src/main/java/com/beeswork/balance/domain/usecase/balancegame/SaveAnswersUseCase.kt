package com.beeswork.balance.domain.usecase.balancegame

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse

interface SaveAnswersUseCase {

    suspend fun invoke(answers: Map<Int, Boolean>): Resource<EmptyResponse>
}