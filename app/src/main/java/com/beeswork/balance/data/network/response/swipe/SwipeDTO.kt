package com.beeswork.balance.data.network.response.swipe

import java.util.*

data class SwipeDTO(
    val id: Long,
    val swiperId: UUID,
    val swipedId: UUID,
    val clicked: Boolean,
    val swiperProfilePhotoKey: String?
)