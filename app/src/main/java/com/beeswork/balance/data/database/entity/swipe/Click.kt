package com.beeswork.balance.data.database.entity.swipe

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "click")
data class Click(
    @PrimaryKey
    val swipedId: UUID,

    val swiperId: UUID
)