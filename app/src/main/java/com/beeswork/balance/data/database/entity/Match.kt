package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime


@Entity(tableName = "match",
        indices = [Index(value = ["matchedId"], unique = true)])
data class Match (

    @PrimaryKey
    val chatId: Long,

    val matchedId: String,
    val unmatched: Boolean,
    val updatedAt: OffsetDateTime,
    val name: String,
    val repPhotoKey: String,
    val blocked: Boolean,
    val deleted: Boolean,
    val accountUpdatedAt: OffsetDateTime,
    val lastReadAt: OffsetDateTime,
    val lastAccountViewedAt: OffsetDateTime
)