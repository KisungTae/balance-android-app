package com.beeswork.balance.data.network.request

import java.util.*

data class PostEmailBody(
    val accountId: UUID,
    val identityToken: UUID,
    val email: String
)