package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "chatProfile")
data class ChatProfile(

    @PrimaryKey
    val id: Int = 0,

    val chatMessageFetchedAt: OffsetDateTime,
    val chatMessagesInsertedAt: OffsetDateTime
)