package com.beeswork.balance.data.network.request

data class ReorderPhotosRequest(
    val accountId: String,
    val identityToken: String,
    val photoOrders: Map<String, Int>
)