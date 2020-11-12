package com.beeswork.balance.data.network.request

data class FCMTokenRequest(
    val accountId: String,
    val identityToken: String,
    val token: String
)