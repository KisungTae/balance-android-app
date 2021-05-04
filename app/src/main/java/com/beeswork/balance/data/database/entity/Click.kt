package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime
import java.util.*

@Entity(tableName = "click")
data class Click(

    @PrimaryKey
    val swiperId: UUID,

    val repPhotoKey: String,
    val updatedAt: OffsetDateTime
)