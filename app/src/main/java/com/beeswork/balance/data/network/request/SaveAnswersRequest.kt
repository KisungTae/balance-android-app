package com.beeswork.balance.data.network.request

data class SaveAnswersRequest(
    val accountId: String,
    val identityToken: String,
    val answers: Map<Int, Boolean>
)