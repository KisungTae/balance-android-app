package com.beeswork.balance.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime


@Entity(tableName = "click")
data class Click(

    @PrimaryKey
    val swipeId: Long,

    val swipedId: String,
    val posted: Boolean,
    val createdAt: OffsetDateTime
)