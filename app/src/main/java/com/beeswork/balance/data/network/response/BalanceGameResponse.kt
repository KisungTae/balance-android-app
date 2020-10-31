package com.beeswork.balance.data.network.response

data class BalanceGameResponse(
    val swipeId: Long,
    val questions: List<QuestionResponse>
)