package com.beeswork.balance.data.database.entity.chat

import androidx.room.*
import com.beeswork.balance.internal.constant.ChatMessageStatus
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

@Entity(
    tableName = "chatMessage",
//    indices = [Index(value = ["status"]), Index(value = ["createdAt", "key"]), Index(value = ["id"], unique = true)]
)
data class ChatMessage(
    val chatId: UUID,
    val body: String,
    var status: ChatMessageStatus,
    var id: UUID,
    var createdAt: OffsetDateTime = OffsetDateTime.parse("9999-12-31T23:59:59.999Z"),

    @PrimaryKey(autoGenerate = true)
    val key: Long = 0
)