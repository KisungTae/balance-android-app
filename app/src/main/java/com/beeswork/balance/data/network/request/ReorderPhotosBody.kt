package com.beeswork.balance.data.network.request

data class ReorderPhotosBody(
    val accountId: String,
    val identityToken: String,
    val photoOrders: Map<String, Int>
)