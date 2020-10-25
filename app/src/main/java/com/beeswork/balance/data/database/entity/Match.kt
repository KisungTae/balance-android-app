package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime


@Entity(tableName = "match",
        indices = [Index(value = ["matchedId"], unique = true)])
data class Match(

    @PrimaryKey
    val chatId: Long,

    val matchedId: String,
    var photoKey: String,
    var name: String,
    var unmatched: Boolean,
    var recentMessage: String,
    var lastRead: OffsetDateTime,
    var 
)