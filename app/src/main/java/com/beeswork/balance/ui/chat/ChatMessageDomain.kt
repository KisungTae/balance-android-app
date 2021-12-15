package com.beeswork.balance.ui.chat

import com.beeswork.balance.internal.constant.ChatMessageStatus
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime
import java.util.*

data class ChatMessageDomain(
    val key: Long,
    val id: UUID?,
    val body: String,
    var status: ChatMessageStatus,
    var isProcessed: Boolean,
    var dateCreatedAt: LocalDate?,
    var timeCreatedAt: LocalTime?,
    var showProfilePhoto: Boolean = true
) {
    companion object {
        fun toSeparator(body: String?): ChatMessageDomain? {
            body?.let {
                return ChatMessageDomain(0L, null, body, ChatMessageStatus.SEPARATOR, false, null, null)
            } ?: return null
        }
    }
}