package com.beeswork.balance.data.database.entity.setting

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "location")
data class Location(

    val latitude: Double,
    val longitude: Double,
    val synced: Boolean,
    val updatedAt: OffsetDateTime,
    val granted: Boolean,

    @PrimaryKey
    val id: Int = ID
) {
    companion object {
        const val ID = 0
    }
}