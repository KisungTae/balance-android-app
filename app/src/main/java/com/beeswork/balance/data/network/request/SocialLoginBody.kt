package com.beeswork.balance.data.network.request

import java.util.*

data class SocialLoginBody(
    val accountId: UUID?,
    val identityToken: UUID?,
    val loginId: String,
    val accessToken: String
)