package com.beeswork.balance.data.network.response.swipe

import org.threeten.bp.OffsetDateTime

class ListSwipesDTO(
    val swipeDTOs: List<SwipeDTO>,
    val swipeCount: Long,
    val swipeCountCountedAt: OffsetDateTime
)