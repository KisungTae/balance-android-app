package com.beeswork.balance.ui.match

import org.threeten.bp.OffsetDateTime
import java.util.*

data class MatchDomain(
    val chatId: Long,
    val matchedId: UUID,
    val active: Boolean,
    val unmatched: Boolean,
    val name: String,
    val repPhotoKey: String,
    val deleted: Boolean,
    val updatedAt: OffsetDateTime,
    val unread: Boolean,
    val recentChatMessage: String,
    val lastReadChatMessageId: Long,
    val valid: Boolean
)