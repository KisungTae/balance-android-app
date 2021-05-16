package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.beeswork.balance.internal.constant.Gender

@Entity(tableName = "swipeFilter")
data class SwipeFilter(
    val gender: Gender = Gender.FEMALE,
    val minAge: Int = MIN_AGE,
    val maxAge: Int = MAX_AGE,
    val distance: Int = DISTANCE,
    var pageIndex: Int = PAGE_INDEX,

    @PrimaryKey
    val id: Int = ID
) {
    companion object {
        const val ID = 0
        const val MIN_AGE = 20
        const val MAX_AGE = 80
        const val DISTANCE = 10
        const val PAGE_INDEX = 0
    }
}