package com.beeswork.balance.data.network.response.profile

data class FetchQuestionsDTO(
    val point: Int,
    val questionDTOs: List<QuestionDTO>
)