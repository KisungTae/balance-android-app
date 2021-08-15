package com.beeswork.balance.data.network.request

import java.util.*

data class RefreshAccessTokenBody(
    val accountId: UUID,
    val refreshToken: String
)