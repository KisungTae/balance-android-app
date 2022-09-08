package com.beeswork.balance.data.network.response.swipe

import java.util.*

data class SwipeNotificationDTO(
    val clicked: Boolean,
    val swiperId: UUID,
    val swiperProfilePhotoKey: String?
)