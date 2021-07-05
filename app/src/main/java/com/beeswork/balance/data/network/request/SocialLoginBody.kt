package com.beeswork.balance.data.network.request

import com.beeswork.balance.internal.constant.LoginType
import java.util.*

data class SocialLoginBody(
    val loginId: String,
    val accessToken: String,
    val loginType: LoginType
)