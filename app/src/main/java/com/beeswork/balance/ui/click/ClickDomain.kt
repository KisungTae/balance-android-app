package com.beeswork.balance.ui.click

import androidx.room.PrimaryKey
import java.util.*

data class ClickDomain(
    val swiperId: UUID,
    val clicked: Boolean,
    val profilePhotoKey: String,
    val type: Type = Type.ITEM
) {

    enum class Type {
        HEADER,
        ITEM
    }

    companion object {
        fun header(): ClickDomain {
            return ClickDomain(UUID.randomUUID(), false, "", Type.HEADER)
        }
    }
}