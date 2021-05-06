package com.beeswork.balance.ui.click

import androidx.room.PrimaryKey
import java.util.*

data class ClickDomain(
    val swiperId: UUID,
    val profilePhotoKey: String
)