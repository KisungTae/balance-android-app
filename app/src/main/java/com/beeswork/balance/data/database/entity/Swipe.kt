package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime
import java.util.*


@Entity(tableName = "swipe")
data class Swipe(

    @PrimaryKey
    val swipedId: UUID,

    val swiperId: UUID?
)