package com.beeswork.balance.data.database.response

import java.util.*

data class NewMatch(
    val swipedId: UUID,
    val swipedName: String,
    val swipedProfilePhotoKey: String?,
    val accountId: UUID?,
    val profilePhotoKey: String?
)