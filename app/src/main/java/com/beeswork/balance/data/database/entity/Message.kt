package com.beeswork.balance.data.database.entity

import androidx.room.*
import com.google.gson.annotations.Expose
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

    @Expose
    val chatId: Long,

    val status: Status,
    val message: String,
    val received: Boolean,
    val read: Boolean,
    val createdAt: OffsetDateTime
) {
    enum class Status {
        SENDING,
        SENT,
        ERROR
    }
}