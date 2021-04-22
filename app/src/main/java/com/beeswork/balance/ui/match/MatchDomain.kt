package com.beeswork.balance.ui.match

import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZonedDateTime
import java.util.*

data class MatchDomain(
    val chatId: Long,
    val matchedId: UUID,
    val active: Boolean,
    val unmatched: Boolean,
    val name: String,
    val repPhotoKey: String?,
    val updatedAt: ZonedDateTime?,
    val unread: Boolean,
    val recentChatMessage: String
)