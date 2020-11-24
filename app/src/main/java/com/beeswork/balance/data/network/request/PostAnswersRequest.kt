package com.beeswork.balance.data.network.request

data class PostAnswersRequest(
    val accountId: String,
    val identityToken: String,
    val answers: Map<Int, Boolean>
)