package com.beeswork.balance.data.network.request.login

import com.beeswork.balance.internal.constant.PushTokenType
import java.util.*

data class RefreshAccessTokenBody(
    val accessToken: String,
    val refreshToken: String
)