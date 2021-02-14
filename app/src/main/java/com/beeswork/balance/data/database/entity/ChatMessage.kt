package com.beeswork.balance.data.database.entity

import androidx.room.*
import com.beeswork.balance.internal.constant.ChatMessageStatus
import org.threeten.bp.OffsetDateTime

@Entity(
    tableName = "chatMessage",
//    foreignKeys = [ForeignKey(
//        entity = Match::class,
//        parentColumns = arrayOf("chatId"),
//        childColumns = arrayOf("chatId"),
//        onDelete = ForeignKey.CASCADE
//    )],
    indices = [Index(value = ["chatId", "id"])]
)
data class ChatMessage(

    @PrimaryKey(autoGenerate = true)
    val messageId: Long = 0,

    val id: Long?,
    val chatId: Long,
    val body: String,
    var status: ChatMessageStatus,
    val createdAt: OffsetDateTime?,
    val updatedAt: OffsetDateTime
) {
    companion object {

        const val TAIL_ID = 0L

        fun getHeadChatMessage(chatId: Long, createdAt: OffsetDateTime): ChatMessage {
            return ChatMessage(
                0,
                null,
                chatId,
                "",
                ChatMessageStatus.HEAD,
                createdAt,
                createdAt
            )
        }

        fun getTailChatMessage(chatId: Long, createdAt: OffsetDateTime): ChatMessage {
            return ChatMessage(
                0,
                TAIL_ID,
                chatId,
                "",
                ChatMessageStatus.TAIL,
                createdAt,
                createdAt
            )
        }
    }
}


// explain query plan select count(cm.id) from chatMessage cm left join `match` m on cm.chatId = m.chatId where cm.chatId = 1 and cm.id > m.lastReadChatMessageId and cm.status = 1
//1	0	0	0	SEARCH TABLE chatMessage AS cm USING INDEX index_chatMessage_chatId_id (chatId=? AND id>?)