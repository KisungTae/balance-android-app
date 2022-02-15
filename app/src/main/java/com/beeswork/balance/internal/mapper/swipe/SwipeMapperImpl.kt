package com.beeswork.balance.internal.mapper.swipe

import com.beeswork.balance.data.database.entity.swipe.Swipe
import com.beeswork.balance.data.network.response.swipe.SwipeDTO
import com.beeswork.balance.internal.util.safeLet
import com.beeswork.balance.ui.swipefragment.SwipeDomain
import java.util.*

class SwipeMapperImpl : SwipeMapper {
    override fun toSwipe(swipeDTO: SwipeDTO): Swipe? {
        return safeLet(
            swipeDTO.id,
            swipeDTO.swipedId,
            swipeDTO.swiperProfilePhotoKey
        ) { id, swipedId, profilePhotoKey ->
            return@safeLet Swipe(id, swipeDTO.swiperId, swipedId, swipeDTO.clicked, profilePhotoKey)
        }
    }

    override fun toSwipeDomain(swipe: Swipe): SwipeDomain {
        return SwipeDomain(swipe.swiperId, swipe.clicked, swipe.swiperProfilePhotoKey)
    }
}