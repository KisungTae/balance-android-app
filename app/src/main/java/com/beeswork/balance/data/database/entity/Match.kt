package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime
import java.util.*


@Entity(
    tableName = "match",
    indices = [Index(value = ["chatId"], unique = true), Index(value = ["swipedId"], unique = true)]
)
data class Match(
    @PrimaryKey
    val chatId: Long,

    val swipedId: UUID,
    var active: Boolean,
    var unmatched: Boolean,
    var name: String,
    var profilePhotoKey: String?,
    var updatedAt: OffsetDateTime?,
    var unread: Boolean = false,
    var recentChatMessage: String = "",
    var lastReadChatMessageId: Long = 0
)