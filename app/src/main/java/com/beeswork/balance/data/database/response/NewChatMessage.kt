package com.beeswork.balance.data.database.response

import java.util.*

data class NewChatMessage(
    val name: String,
    val id: UUID,
    val repPhotoKey: String?,
    val body: String
)