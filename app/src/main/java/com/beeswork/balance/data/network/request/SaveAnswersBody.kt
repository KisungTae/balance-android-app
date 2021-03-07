package com.beeswork.balance.data.network.request

data class SaveAnswersBody(
    val accountId: String,
    val identityToken: String,
    val answers: Map<Int, Boolean>
)