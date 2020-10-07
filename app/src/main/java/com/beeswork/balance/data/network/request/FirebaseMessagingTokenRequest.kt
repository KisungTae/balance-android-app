package com.beeswork.balance.data.network.request

data class FirebaseMessagingTokenRequest(
    val accountId: String,
    val email: String,
    val token: String
)