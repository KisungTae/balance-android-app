package com.beeswork.balance.ui.chat

import com.beeswork.balance.internal.constant.ChatMessageStatus
import org.threeten.bp.OffsetDateTime

data class ChatMessageDomain(
    val id: Long?,
    val body: String,
    var status: ChatMessageStatus,
    val createdAt: OffsetDateTime?
)