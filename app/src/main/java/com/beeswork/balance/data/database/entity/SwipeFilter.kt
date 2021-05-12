package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.beeswork.balance.internal.constant.Gender

@Entity(tableName = "swipeFilter")
data class SwipeFilter(
    val gender: Gender,
    val minAge: Int,
    val maxAge: Int,
    val distance: Int,

    @PrimaryKey
    val id: Int = 0
)