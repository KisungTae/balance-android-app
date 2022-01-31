package com.beeswork.balance.data.database.entity.click

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime
import java.util.*

@Entity(tableName = "click")
data class Click(

    @PrimaryKey
    val id: Long,

    val swiperId: UUID,
    val swipedId: UUID,
    val name: String,
    val clicked: Boolean,
    var profilePhotoKey: String
)