package com.beeswork.balance.data.network.request.swipe

import java.util.*

data class SwipeBody(
    val accountId: UUID,
    val swipedId: UUID?
)