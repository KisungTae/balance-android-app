package com.beeswork.balance.data.database.entity.chat

import androidx.room.*
import com.beeswork.balance.internal.constant.ChatMessageStatus
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

@Entity(
    tableName = "chatMessage",
    indices = [Index(value = ["status"]), Index(value = ["createdAt", "key"])]
)
data class ChatMessage(
    val chatId: Long,
    val body: String,
    var status: ChatMessageStatus,
    var id: UUID?,
    var createdAt: OffsetDateTime = OffsetDateTime.parse("9999-12-31T23:59:59.999Z"),

    @PrimaryKey(autoGenerate = true)
    val key: Long = 0
) {
    constructor(chatId: Long, body: String, status: ChatMessageStatus) : this(chatId, body, status, null)

    fun isProcessed(): Boolean {
        return (status == ChatMessageStatus.RECEIVED || status == ChatMessageStatus.SENT)
    }
}


// explain query plan select count(cm.id) from chatMessage cm left join `match` m on cm.chatId = m.chatId where cm.chatId = 1 and cm.id > m.lastReadChatMessageId and cm.status = 1
//1	0	0	0	SEARCH TABLE chatMessage AS cm USING INDEX index_chatMessage_chatId_id (chatId=? AND id>?)