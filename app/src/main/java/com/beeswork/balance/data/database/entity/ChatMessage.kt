package com.beeswork.balance.data.database.entity

import androidx.room.*
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.google.firebase.encoders.annotations.Encodable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.IgnoredOnParcel
import org.threeten.bp.OffsetDateTime

@Entity(
    tableName = "chatMessage",
    foreignKeys = [ForeignKey(
        entity = Match::class,
        parentColumns = arrayOf("chatId"),
        childColumns = arrayOf("chatId"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["id", "chatId"])]
)
data class ChatMessage(

    @PrimaryKey(autoGenerate = true)
    val messageId: Long? = null,

    val id: Long?,
    val chatId: Long,
    val body: String,
    var status: ChatMessageStatus,
    val createdAt: OffsetDateTime?,
    val updatedAt: OffsetDateTime?
)