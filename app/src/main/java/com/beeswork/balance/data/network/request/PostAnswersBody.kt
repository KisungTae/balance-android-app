package com.beeswork.balance.data.network.request

data class PostAnswersBody(
    val accountId: String,
    val identityToken: String,
    val answers: Map<Int, Boolean>
)