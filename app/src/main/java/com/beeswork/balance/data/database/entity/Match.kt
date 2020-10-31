package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime


@Entity(tableName = "match",
        indices = [Index(value = ["matchedId"], unique = true)])
data class Match @JvmOverloads constructor (

    @PrimaryKey
    val chatId: Long,

    val matchedId: String,
    var photoKey: String,
    val name: String,
    var unmatched: Boolean,
    var recentMessage: String,
    var unreadMessageCount: Int,
    var lastReadAt: OffsetDateTime,
    var lastReceivedAt: OffsetDateTime,

    @Ignore
    val updatedAt: String? = null
)