package com.beeswork.balance.domain.uistate.chat

import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.constant.DateTimePattern
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import java.util.*

data class ChatMessageItemUIState(
    val tag: UUID?,
    val body: String,
    var status: ChatMessageStatus,
    var dateCreatedAt: LocalDate?,
    var timeCreatedAt: LocalTime?,
    var showProfilePhoto: Boolean = true,
    var showTime: Boolean = true,
    var topMargin: Int = 0
) {

    companion object {
        fun ofSeparator(dateCreatedAt: LocalDate): ChatMessageItemUIState {
            return ChatMessageItemUIState(null, "", ChatMessageStatus.SEPARATOR, dateCreatedAt, null)
        }
    }
}