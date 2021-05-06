package com.beeswork.balance.data.database.tuple

import java.util.*

data class MatchProfileTuple(
    val swipedId: UUID,
    val name: String,
    val profilePhotoKey: String?
)