package com.beeswork.balance.data.network.request

import java.util.*

data class SwipeBody(
    val accountId: UUID?,
    val identityToken: UUID?,
    val swipedId: UUID?
)