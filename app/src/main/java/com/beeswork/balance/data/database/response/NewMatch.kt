package com.beeswork.balance.data.database.response

import java.util.*

data class NewMatch(
    val matchedId: UUID,
    val matchedName: String,
    val matchedRepPhotoKey: String,
    val matcherId: UUID,
    val matcherRepPhotoKey: String
)