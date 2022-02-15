package com.beeswork.balance.data.network.response.swipe

import java.util.*

data class SwipeDTO(
    val id: Long,
    val swiperId: UUID,
    var swipedId: UUID?,
    val clicked: Boolean,
    val swiperDeleted: Boolean,
    val swiperProfilePhotoKey: String?,
)