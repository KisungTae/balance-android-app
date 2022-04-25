package com.beeswork.balance.data.database.entity.card

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.beeswork.balance.internal.constant.Gender
import java.util.*

@Entity(tableName = "cardFilter")
data class CardFilter(
    @PrimaryKey
    val accountId: UUID,

    val gender: Boolean? = null,
    val minAge: Int = MIN_AGE,
    val maxAge: Int = MAX_AGE,
    val distance: Int = MAX_DISTANCE,
    var pageIndex: Int = PAGE_INDEX
) {
    companion object {
        const val MIN_AGE = 20
        const val MAX_AGE = 80
        const val MIN_DISTANCE = 1
        const val MAX_DISTANCE = 30
        const val PAGE_INDEX = 0
    }
}