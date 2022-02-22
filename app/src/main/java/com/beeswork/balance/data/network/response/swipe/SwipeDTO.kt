package com.beeswork.balance.data.network.response.swipe

import org.threeten.bp.OffsetDateTime
import java.util.*

data class SwipeDTO(
    val id: Long,
    val swiperId: UUID,
    val swipedId: UUID,
    val clicked: Boolean,
    val updatedAt: OffsetDateTime?,
    val swiperDeleted: Boolean,
    val swiperProfilePhotoKey: String?
)