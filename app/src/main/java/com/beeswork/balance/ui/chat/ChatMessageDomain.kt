package com.beeswork.balance.ui.chat

import com.beeswork.balance.internal.constant.ChatMessageStatus
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime

data class ChatMessageDomain(
    val key: Long,
    val id: Long?,
    val body: String,
    var status: ChatMessageStatus,
    val dateCreatedAt: LocalDate?,
    val timeCreatedAt: LocalTime?
) {
    companion object {
        fun toSeparator(body: String): ChatMessageDomain {
            return ChatMessageDomain(0L, null, body, ChatMessageStatus.SEPARATOR, null, null)
        }
    }
}