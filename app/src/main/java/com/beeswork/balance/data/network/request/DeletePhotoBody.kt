package com.beeswork.balance.data.network.request

data class DeletePhotoBody(
    val accountId: String,
    val identityToken: String,
    val photoKey: String
)