package com.beeswork.balance.data.network.request.login

import com.beeswork.balance.internal.constant.PushTokenType

data class LoginWithRefreshTokenBody(
    val accessToken: String,
    val refreshToken: String,
    val pushToken: String?,
    val pushTokenType: PushTokenType = PushTokenType.FCM
)