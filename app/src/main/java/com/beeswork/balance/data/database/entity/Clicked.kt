package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "clicked")
data class Clicked(

    @PrimaryKey
    val swiperId: String,

    val photoKey: String,
    val updatedAt: OffsetDateTime
)