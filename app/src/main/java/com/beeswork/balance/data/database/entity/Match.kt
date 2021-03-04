package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime
import java.util.*


@Entity(
    tableName = "match",
    indices = [Index(value = ["matchedId"], unique = true)]
)
data class Match(

    @PrimaryKey
    val chatId: Long,

    val matchedId: UUID,
    val active: Boolean,
    val unmatched: Boolean,
    val name: String,
    val repPhotoKey: String,
    val deleted: Boolean,
    var updatedAt: OffsetDateTime,
    var unread: Boolean = false,
    var recentMessage: String = "",
    var lastReadChatMessageId: Long = 0
)