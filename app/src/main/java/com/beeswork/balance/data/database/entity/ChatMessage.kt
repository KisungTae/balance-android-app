package com.beeswork.balance.data.database.entity

import androidx.room.*
import com.google.firebase.encoders.annotations.Encodable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.IgnoredOnParcel
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "chatMessage",
        foreignKeys = [ForeignKey(entity = Match::class,
            parentColumns = arrayOf("chatId"),
            childColumns = arrayOf("chatId"),
            onDelete = ForeignKey.CASCADE)],
        indices = [Index(value = ["chatId"])])
data class ChatMessage(

    @PrimaryKey(autoGenerate = true)
    val messageId: Long? = null,

    val id: Long?,
    val chatId: Long,
    val body: String,
    val status: Status,
    val received: Boolean,
    val read: Boolean,
    val createdAt: OffsetDateTime?
) {

    @Ignore
    var recipientId: String? = null

    enum class Status {
        SENDING,
        SENT,
        ERROR
    }
}