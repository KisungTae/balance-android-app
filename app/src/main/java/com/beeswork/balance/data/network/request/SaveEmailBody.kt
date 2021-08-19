package com.beeswork.balance.data.network.request

import java.util.*

data class SaveEmailBody(
    val accountId: UUID,
    val email: String
)