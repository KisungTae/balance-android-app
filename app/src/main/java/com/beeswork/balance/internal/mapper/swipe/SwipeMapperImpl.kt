package com.beeswork.balance.internal.mapper.swipe

import com.beeswork.balance.data.database.entity.swipe.Swipe
import com.beeswork.balance.data.network.response.swipe.SwipeDTO
import com.beeswork.balance.domain.uistate.swipe.SwipeNotificationUIState
import com.beeswork.balance.internal.constant.EndPoint
import com.beeswork.balance.domain.uistate.swipe.SwipeUIState

class SwipeMapperImpl : SwipeMapper {
    override fun toSwipe(swipeDTO: SwipeDTO): Swipe {
        return Swipe(swipeDTO.id, swipeDTO.swiperId, swipeDTO.swipedId, swipeDTO.clicked, swipeDTO.swiperProfilePhotoKey)
    }

    override fun toSwipeUIStateItem(swipe: Swipe): SwipeUIState {
        return SwipeUIState.Item(
            swipe.id,
            swipe.swiperId,
            swipe.clicked,
            EndPoint.ofPhoto(swipe.swiperId, swipe.swiperProfilePhotoKey)
        )
    }

    override fun toSwipeNotificationUIState(swipe: Swipe): SwipeNotificationUIState {
        return SwipeNotificationUIState(
            EndPoint.ofPhoto(swipe.swiperId, swipe.swiperProfilePhotoKey),
            swipe.clicked
        )
    }
}