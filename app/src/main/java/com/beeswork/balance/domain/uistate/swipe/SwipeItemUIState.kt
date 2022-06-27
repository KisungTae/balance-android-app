package com.beeswork.balance.domain.uistate.swipe

import java.util.*

data class SwipeItemUIState(
    val id: Long,
    val swiperId: UUID,
    val clicked: Boolean,
    val swiperProfilePhotoUrl: String?,
    val type: Type = Type.ITEM
) {

    enum class Type {
        HEADER,
        ITEM
    }

    companion object {
        fun asHeader(): SwipeItemUIState {
            return SwipeItemUIState(0, UUID.randomUUID(), false, null, Type.HEADER)
        }
    }
}