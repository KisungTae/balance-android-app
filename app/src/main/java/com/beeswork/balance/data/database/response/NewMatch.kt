package com.beeswork.balance.data.database.response

import java.util.*

data class NewMatch(
    val swipedId: UUID,
    val swipedName: String,
    val swipedRepPhotoKey: String?,
    val accountId: UUID?,
    val repPhotoKey: String?
)