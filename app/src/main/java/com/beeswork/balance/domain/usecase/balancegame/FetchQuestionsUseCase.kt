package com.beeswork.balance.domain.usecase.balancegame

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.profile.FetchQuestionsDTO
import com.beeswork.balance.data.network.response.profile.QuestionDTO
import com.beeswork.balance.domain.uistate.balancegame.QuestionItemUIState

interface FetchQuestionsUseCase {

    suspend fun invoke(): Resource<FetchQuestionsDTO>
}