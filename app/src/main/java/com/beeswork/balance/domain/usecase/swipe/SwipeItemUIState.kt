package com.beeswork.balance.domain.usecase.swipe

import java.util.*

data class SwipeItemUIState(
    val swiperId: UUID,
    val swiperProfilePhotoUrl: String?,
    val clicked: Boolean,
    val type: Type = Type.ITEM
) {

    enum class Type {
        HEADER,
        ITEM
    }

    companion object {
        fun ofHeader(): SwipeItemUIState {
            return SwipeItemUIState(UUID.randomUUID(), null, false, Type.HEADER)
        }
    }
}