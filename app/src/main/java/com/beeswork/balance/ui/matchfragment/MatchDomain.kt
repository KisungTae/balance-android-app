package com.beeswork.balance.ui.matchfragment

import org.threeten.bp.ZonedDateTime
import java.util.*

data class MatchDomain(
    val chatId: Long,
    val swipedId: UUID,
    val active: Boolean,
    val unmatched: Boolean,
    val swipedName: String,
    val swipedProfilePhotoKey: String?,
    val updatedAt: ZonedDateTime?,
    val unread: Boolean,
    val recentChatMessage: String
)