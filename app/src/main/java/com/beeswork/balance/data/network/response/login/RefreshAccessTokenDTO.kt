package com.beeswork.balance.data.network.response.login

data class RefreshAccessTokenDTO(
    val accessToken: String,
    val refreshToken: String?
)