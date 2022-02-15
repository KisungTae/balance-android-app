package com.beeswork.balance.ui.swipefragment

import java.util.*

data class SwipeDomain(
    val swiperId: UUID,
    val clicked: Boolean,
    val swiperProfilePhotoKey: String,
    val type: Type = Type.ITEM
) {

    enum class Type {
        HEADER,
        ITEM
    }

    companion object {
        fun header(): SwipeDomain {
            return SwipeDomain(UUID.randomUUID(), false, "", Type.HEADER)
        }
    }
}