package com.beeswork.balance.domain.uistate.swipe

import com.beeswork.balance.ui.common.paging.Pageable
import java.util.*

sealed class SwipeUIState(
    val id: Long
): Pageable {

    override fun getKey(): Long {
        return id
    }

    class Header : SwipeUIState(Long.MIN_VALUE)

    class Item(
        id: Long,
        val swiperId: UUID,
        val clicked: Boolean,
        val swiperProfilePhotoURL: String?,
    ): SwipeUIState(id)

    class Footer: SwipeUIState(-1L)
}