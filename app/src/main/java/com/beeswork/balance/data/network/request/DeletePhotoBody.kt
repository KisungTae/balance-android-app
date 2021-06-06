package com.beeswork.balance.data.network.request

import java.util.*

data class DeletePhotoBody(
    val accountId: UUID?,
    val identityToken: UUID?,
    val photoKey: String
)