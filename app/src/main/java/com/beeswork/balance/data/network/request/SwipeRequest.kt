package com.beeswork.balance.data.network.request

data class SwipeRequest(
    val accountId: String,
    val email: String,
    val swipedId: String
)