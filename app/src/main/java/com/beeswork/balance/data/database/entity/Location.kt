package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

const val LOCATION_ID = 0

@Entity(tableName = "location")
data class Location(

    val latitude: Double,
    val longitude: Double,
    val synced: Boolean,
    val updatedAt: OffsetDateTime,

    @PrimaryKey
    val id: Int = LOCATION_ID
)