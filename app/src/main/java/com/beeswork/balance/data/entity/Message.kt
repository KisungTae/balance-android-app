package com.beeswork.balance.data.entity

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
    val isReceived: Boolean,
    val message: String,
    val createdAt: OffsetDateTime? = null
)