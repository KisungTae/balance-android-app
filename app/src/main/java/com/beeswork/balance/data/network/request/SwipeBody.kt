package com.beeswork.balance.data.network.request

data class SwipeBody(
    val accountId: String,
    val identityToken: String,
    val swipeId: Long?,
    val swipedId: String
)