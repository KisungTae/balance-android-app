package com.beeswork.balance.data.network.request

import retrofit2.http.Query

data class FetchPreSignedUrlRequest(
    val accountId: String,
    val identityToken: String,
    val photoKey: String
)