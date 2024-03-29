package com.beeswork.balance.data.network.request.login

import com.beeswork.balance.internal.constant.LoginType
import com.beeswork.balance.internal.constant.PushTokenType

data class SocialLoginBody(
    val loginId: String,
    val accessToken: String,
    val loginType: LoginType,
    val pushToken: String?,
    val pushTokenType: PushTokenType = PushTokenType.FCM
)