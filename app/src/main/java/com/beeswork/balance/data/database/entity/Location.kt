package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity(tableName = "location")
data class Location (

    val synced: Boolean,
    val updatedAt: OffsetDateTime,
    val latitude: Double,
    val longitude: Double,

    @PrimaryKey
    val id: Int = 0
)