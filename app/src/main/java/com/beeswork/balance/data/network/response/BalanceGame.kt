package com.beeswork.balance.data.network.response

data class BalanceGame(
    val swipeId: Long,
    val questions: List<Question>
)