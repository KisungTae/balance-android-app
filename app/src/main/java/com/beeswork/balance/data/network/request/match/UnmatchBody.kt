package com.beeswork.balance.data.network.request.match

import java.util.*

data class UnmatchBody(
    val accountId: UUID,
    val swipedId: UUID
)