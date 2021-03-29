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

    val chatId: Long,
    val body: String,
    var status: ChatMessageStatus,
    val createdAt: OffsetDateTime?,

    val id: Long = Long.MAX_VALUE,

    @PrimaryKey(autoGenerate = true)
    val key: Long = 0
)


// explain query plan select count(cm.id) from chatMessage cm left join `match` m on cm.chatId = m.chatId where cm.chatId = 1 and cm.id > m.lastReadChatMessageId and cm.status = 1
//1	0	0	0	SEARCH TABLE chatMessage AS cm USING INDEX index_chatMessage_chatId_id (chatId=? AND id>?)