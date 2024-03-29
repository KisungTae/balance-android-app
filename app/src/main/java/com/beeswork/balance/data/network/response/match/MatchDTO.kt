package com.beeswork.balance.data.network.response.match

import org.threeten.bp.OffsetDateTime
import java.util.*

data class MatchDTO(
    val id: Long,
    val chatId: UUID,
    val swiperId: UUID,
    val swipedId: UUID,
    val unmatched: Boolean,
    val lastReceivedChatMessageId: Long,
    val lastReadReceivedChatMessageId: Long,
    val lastReadByChatMessageId: Long,
    val lastChatMessageId: Long,
    val lastChatMessageBody: String?,
    val lastChatMessageCreatedAt: OffsetDateTime?,
    val createdAt: OffsetDateTime,
    val swipedName: String?,
    val swipedProfilePhotoKey: String?,
    val swipedDeleted: Boolean
)