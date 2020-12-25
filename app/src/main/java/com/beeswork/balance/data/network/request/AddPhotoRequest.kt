package com.beeswork.balance.data.network.request

import retrofit2.http.Query

data class AddPhotoRequest(
    val accountId: String,
    val identityToken: String,
    val photoKey: String,
    val sequence: Int
)