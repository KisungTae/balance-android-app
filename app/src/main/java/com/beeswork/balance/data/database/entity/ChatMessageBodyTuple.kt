package com.beeswork.balance.data.database.entity

import com.beeswork.balance.internal.constant.ChatMessageStatus
import org.threeten.bp.OffsetDateTime

data class ChatMessageBodyTuple(
    val body: String,
    val createdAt: OffsetDateTime,
)