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
    var active: Boolean,
    var unmatched: Boolean,
    var name: String,
    var repPhotoKey: String,
    var deleted: Boolean,
    var updatedAt: OffsetDateTime,
    var unread: Boolean = false,
    var recentMessage: String = "",
    var lastReadChatMessageId: Long = 0
) {
    fun valid(): Boolean {
        return (!unmatched && !deleted)
    }
}