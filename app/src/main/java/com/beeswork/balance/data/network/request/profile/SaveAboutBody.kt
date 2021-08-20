package com.beeswork.balance.data.network.request.profile

import java.util.*

data class SaveAboutBody(
    val accountId: UUID,
    val height: Int?,
    val about: String
)