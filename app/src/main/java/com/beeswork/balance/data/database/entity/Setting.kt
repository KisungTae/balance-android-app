package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "setting")
data class Setting(
    val email: String?,
    val emailSynced: Boolean = false,
    val matchPush: Boolean = false,
    val clickedPush: Boolean = false,
    val chatMessagePush: Boolean = false,
    val synced: Boolean = false,

    @PrimaryKey
    val id: Int = ID
) {
    companion object {
        const val ID = 0
    }
}