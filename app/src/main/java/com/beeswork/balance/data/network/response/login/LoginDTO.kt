package com.beeswork.balance.data.network.response.login

import java.util.*

data class LoginDTO(
    val accountId: UUID,
    val identityToken: UUID,
    val profileExists: Boolean,
    val jwtToken: String
)