package com.beeswork.balance.data.network.request

import java.util.*

data class SaveFCMTokenBody(
    val accountId: UUID,
    val token: String
)