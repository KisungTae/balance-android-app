package com.beeswork.balance.data.network.request

data class SwipeRequest(
    val swiperId: String,
    val swiperEmail: String,
    val swipedId: String,
    val swipeId: Long? = null
)