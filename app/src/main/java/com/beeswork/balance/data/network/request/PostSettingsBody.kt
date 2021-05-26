package com.beeswork.balance.data.network.request

import java.util.*

data class PostSettingsBody(
    val accountId: UUID?,
    val identityToken: UUID?,
    val email: String?
)