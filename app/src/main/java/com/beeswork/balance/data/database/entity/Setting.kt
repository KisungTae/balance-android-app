package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "setting")
data class Setting(
    val email: String? = null,
    val emailSynced: Boolean = true,
    val matchPush: Boolean = true,
    val matchPushSynced: Boolean = true,
    val clickedPush: Boolean = true,
    val clickedPushSynced: Boolean = true,
    val chatMessagePush: Boolean = true,
    val chatMessagePushSynced: Boolean = true,

    @PrimaryKey
    val id: Int = ID
) {
    companion object {
        const val ID = 0
    }
}