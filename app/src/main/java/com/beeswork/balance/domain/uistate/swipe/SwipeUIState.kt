package com.beeswork.balance.domain.uistate.swipe

import com.beeswork.balance.ui.common.page.PageLoadType
import java.util.*

sealed class SwipeUIState {

    data class Item(
        val id: Long,
        val swiperId: UUID,
        val clicked: Boolean,
        val swiperProfilePhotoURL: String?,
    ) : SwipeUIState()

    object Header : SwipeUIState()

    object PageLoadStateLoading : SwipeUIState()

    class PageLoadStateError(
        val pageLoadType: PageLoadType,
        val exception: Throwable?
    ) : SwipeUIState()

}