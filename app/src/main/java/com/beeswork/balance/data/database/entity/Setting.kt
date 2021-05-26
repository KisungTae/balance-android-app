package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "setting")
data class Setting(
    val email: String?,
    val synced: Boolean,

    @PrimaryKey
    val id: Int = ID
) {
    companion object {
        const val ID = 0
    }
}