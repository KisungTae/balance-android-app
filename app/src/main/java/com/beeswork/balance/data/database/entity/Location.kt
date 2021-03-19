package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "location")
data class Location(

    val latitude: Double,
    val longitude: Double,
    val synced: Boolean,
    val updatedAt: OffsetDateTime,

    @PrimaryKey
    val id: Int = ID
) {
    companion object {
        const val ID = 0
    }
}