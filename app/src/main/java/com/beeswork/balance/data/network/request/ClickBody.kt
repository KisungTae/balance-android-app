package com.beeswork.balance.data.network.request

import retrofit2.http.FieldMap

data class ClickBody(
    val accountId: String,
    val identityToken: String,
    val swipedId: String,
    val swipeId: Long,
    val answers: Map<Int, Boolean>
)