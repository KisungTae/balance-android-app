package com.beeswork.balance.data.network.response.swipe

import org.threeten.bp.OffsetDateTime
import java.util.*

data class SwipeDTO(
    val swiperId: UUID,
    val deleted: Boolean,
    val profilePhotoKey: String,
    val updatedAt: OffsetDateTime
)