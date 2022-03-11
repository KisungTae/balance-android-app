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
    val status: ChatMessageStatus,
    val tag: UUID?,
    val createdAt: OffsetDateTime? = null,
    val id: Long = Long.MAX_VALUE,

    @PrimaryKey(autoGenerate = true)
    val sequence: Long = 0
)