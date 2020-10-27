package com.beeswork.balance.data.database.entity

import androidx.room.*
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "message",
        foreignKeys = [ForeignKey(entity = Match::class,
            parentColumns = arrayOf("chatId"),
            childColumns = arrayOf("chatId"),
            onDelete = ForeignKey.CASCADE)],
        indices = [Index(value = ["chatId"])])
data class Message(

    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,

    val chatId: Long,
    val message: String,
    val isReceived: Boolean,
    val isRead: Boolean,
    val sentAt: OffsetDateTime
)