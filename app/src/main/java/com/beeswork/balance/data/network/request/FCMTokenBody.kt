package com.beeswork.balance.data.network.request

data class FCMTokenBody(
    val accountId: String,
    val identityToken: String,
    val token: String
)