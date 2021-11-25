package com.beeswork.balance.data.network.request.login

import java.util.*

data class RefreshAccessTokenBody(
    val accessToken: String,
    val refreshToken: String
)