package com.beeswork.balance.data.network.request

import retrofit2.http.FieldMap

data class ClickRequest(
    val accountId: String,
    val email: String,
    val swipedId: String,
    val swipeId: Long,
    val answers: Map<Long, Boolean>
)