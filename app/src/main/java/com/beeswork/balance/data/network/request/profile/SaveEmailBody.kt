package com.beeswork.balance.data.network.request.profile

import java.util.*

data class SaveEmailBody(
    val accountId: UUID,
    val email: String
)