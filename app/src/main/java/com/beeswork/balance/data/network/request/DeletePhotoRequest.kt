package com.beeswork.balance.data.network.request

data class DeletePhotoRequest(
    val accountId: String,
    val identityToken: String,
    val photoKey: String
)