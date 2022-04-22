package com.beeswork.balance.internal.mapper.swipe

import com.beeswork.balance.data.database.entity.swipe.Swipe
import com.beeswork.balance.data.network.response.swipe.SwipeDTO
import com.beeswork.balance.domain.usecase.swipe.SwipeNotificationUIState
import com.beeswork.balance.internal.constant.EndPoint
import com.beeswork.balance.internal.util.safeLet
import com.beeswork.balance.ui.swipefragment.SwipeItemUIState
import java.util.*

class SwipeMapperImpl : SwipeMapper {
    override fun toSwipe(swipeDTO: SwipeDTO): Swipe? {
        return safeLet(
            swipeDTO.id,
            swipeDTO.swipedId,
            swipeDTO.clicked
        ) { id, swipedId, clicked ->
            return@safeLet Swipe(id, swipeDTO.swiperId, swipedId, clicked, swipeDTO.swiperProfilePhotoKey)
        }
    }

    override fun toSwipeItemUIState(swipe: Swipe, photoBucketUrl: String?): SwipeItemUIState {
        return SwipeItemUIState(
            swipe.swiperId,
            swipe.clicked,
            EndPoint.ofPhoto(photoBucketUrl, swipe.swiperId, swipe.swiperProfilePhotoKey)
        )
    }

    override fun toSwipeNotificationUIState(swipe: Swipe, photoBucketUrl: String?): SwipeNotificationUIState {
        return SwipeNotificationUIState(
            EndPoint.ofPhoto(photoBucketUrl, swipe.swiperId, swipe.swiperProfilePhotoKey),
            swipe.clicked
        )
    }
}