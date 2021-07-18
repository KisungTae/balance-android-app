package com.beeswork.balance.data.network.response.photo

import java.util.*

data class PhotoDTO(
    val key: String,
    val accountId: UUID,
    val sequence: Int
)