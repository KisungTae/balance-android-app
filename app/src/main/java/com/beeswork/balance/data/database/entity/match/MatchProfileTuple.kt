package com.beeswork.balance.data.database.entity.match

import java.util.*

data class MatchProfileTuple(
    val swipedId: UUID,
    val name: String,
    val profilePhotoKey: String?
)