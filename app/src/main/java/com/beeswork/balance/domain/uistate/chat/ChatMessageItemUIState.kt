package com.beeswork.balance.domain.uistate.chat

import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.constant.DateTimePattern
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import java.util.*

data class ChatMessageItemUIState(
    val tag: UUID,
    val body: String,
    var status: ChatMessageStatus,
    var dateCreatedAt: LocalDate?,
    var timeCreatedAt: LocalTime?,
    var showProfilePhoto: Boolean = true,
    var showTime: Boolean = true,
    var topMargin: Int = 0
) {
    fun formatTimeCreatedAt(): String {
        if (!showTime) {
            return ""
        }
        return timeCreatedAt?.format(DateTimePattern.ofTimeWithMeridiem()) ?: ""
    }

    companion object {
        fun ofSeparator(body: String): ChatMessageItemUIState {
            return ChatMessageItemUIState(UUID.randomUUID(), body, ChatMessageStatus.SEPARATOR, null, null)
        }
    }
}