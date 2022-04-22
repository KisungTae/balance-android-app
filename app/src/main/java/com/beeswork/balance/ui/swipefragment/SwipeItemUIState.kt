package com.beeswork.balance.ui.swipefragment

import java.util.*

data class SwipeItemUIState(
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
            return SwipeItemUIState(UUID.randomUUID(), false, "", Type.HEADER)
        }
    }
}