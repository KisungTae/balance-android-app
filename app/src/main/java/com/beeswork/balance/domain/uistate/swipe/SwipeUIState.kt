package com.beeswork.balance.domain.uistate.swipe

import com.beeswork.balance.ui.common.paging.ItemKeyPageable
import java.util.*

sealed class SwipeUIState : ItemKeyPageable<Long> {

    data class Header(
        override val key: Long = Long.MIN_VALUE
    ) : SwipeUIState()

    data class Item(
        override val key: Long,
        val swiperId: UUID,
        val clicked: Boolean,
        val swiperProfilePhotoURL: String?,
    ) : SwipeUIState()

    data class Footer(
        override val key: Long = Long.MIN_VALUE
    ) : SwipeUIState()
}