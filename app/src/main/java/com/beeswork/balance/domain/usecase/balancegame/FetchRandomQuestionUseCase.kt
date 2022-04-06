package com.beeswork.balance.domain.usecase.balancegame

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.profile.QuestionDTO

interface FetchRandomQuestionUseCase {

    suspend fun invoke(questionIds: List<Int>): Resource<QuestionDTO>
}