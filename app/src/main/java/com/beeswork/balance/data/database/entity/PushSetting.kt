package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime
import java.util.*

@Entity(tableName = "pushSetting")
data class PushSetting(

    @PrimaryKey
    val accountId: UUID,

    val matchPush: Boolean = true,
    val clickedPush: Boolean = true,
    val chatMessagePush: Boolean = true,
    val emailPush: Boolean = true,
    val synced: Boolean = true
)