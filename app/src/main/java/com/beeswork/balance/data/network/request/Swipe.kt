package com.beeswork.balance.data.network.request

data class Swipe(
    val swiperId: String,
    val swiperEmail: String,
    val swipedId: String,
    val swipeId: Long? = null
)