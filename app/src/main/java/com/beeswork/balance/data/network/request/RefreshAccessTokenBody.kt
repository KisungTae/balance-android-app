package com.beeswork.balance.data.network.request

data class RefreshAccessTokenBody(
    val accessToken: String,
    val refreshToken: String
)