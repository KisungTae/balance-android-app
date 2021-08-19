package com.beeswork.balance.data.network.request

import java.util.*

data class OrderPhotosBody(
    val accountId: UUID,
    val photoOrders: Map<String, Int>
)