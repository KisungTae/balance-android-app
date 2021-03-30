package com.beeswork.balance.ui.match

import java.util.*

data class MatchProfileDomain(
    val matchedId: UUID,
    var name: String,
    val repPhotoKey: String?,
)