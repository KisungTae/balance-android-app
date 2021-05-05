package com.beeswork.balance.data.network.request

import java.util.*

data class PostFCMTokenBody(
    val accountId: UUID?,
    val identityToken: UUID?,
    val token: String
)