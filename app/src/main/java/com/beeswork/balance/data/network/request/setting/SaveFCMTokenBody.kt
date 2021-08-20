package com.beeswork.balance.data.network.request.setting

import java.util.*

data class SaveFCMTokenBody(
    val accountId: UUID,
    val token: String
)