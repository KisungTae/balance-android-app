package com.beeswork.balance.data.network.request

data class FCMTokenRequest(
    val accountId: String,
    val email: String,
    val token: String
)