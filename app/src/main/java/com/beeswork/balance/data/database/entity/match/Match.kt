package com.beeswork.balance.data.database.entity.match

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime
import java.util.*


@Entity(
    tableName = "match",
    indices = [Index(value = ["swiperId"], unique = false), Index(value = ["chatId"], unique = true)]
)
data class Match(

    @PrimaryKey
    val id: Long,

    val chatId: UUID,
    val swiperId: UUID,
    val swipedId: UUID,
    val unmatched: Boolean,
    val lastReadChatMessageId: Long,
    val lastChatMessageId: Long,
    val lastChatMessageBody: String?,
    val swipedName: String,
    val swipedProfilePhotoKey: String?,
    val swipedDeleted: Boolean
)