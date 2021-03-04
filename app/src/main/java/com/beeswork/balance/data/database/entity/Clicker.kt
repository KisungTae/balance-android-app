package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime
import java.util.*

@Entity(tableName = "clicker")
data class Clicker(

    @PrimaryKey
    val id: UUID,

    val repPhotoKey: String,
    val updatedAt: OffsetDateTime
)