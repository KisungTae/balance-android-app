package com.beeswork.balance.data.database.entity.match

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.beeswork.balance.data.network.response.match.MatchDTO
import org.threeten.bp.OffsetDateTime
import java.util.*


@Entity(
    tableName = "match",
    indices = [Index(value = ["swiperId"], unique = false), Index(value = ["chatId"], unique = true)]
)
data class Match(

    @PrimaryKey
    val id: Long,

    val chatId: UUID,
    val swiperId: UUID,
    val swipedId: UUID,
    val unmatched: Boolean,
    var lastReceivedChatMessageId: Long,
    val lastReadReceivedChatMessageId: Long,
    val lastReadByChatMessageId: Long,
    var lastChatMessageId: Long,
    var lastChatMessageBody: String?,
    val createdAt: OffsetDateTime,
    val swipedName: String?,
    val swipedProfilePhotoKey: String?,
    val swipedDeleted: Boolean
) {
    fun isEqualTo(matchDTO: MatchDTO): Boolean {
        if (this.id == matchDTO.id
            && this.chatId == matchDTO.chatId
            && this.swiperId == matchDTO.swiperId
            && this.swipedId == matchDTO.swipedId
            && this.unmatched == matchDTO.unmatched
            && this.lastReceivedChatMessageId == matchDTO.lastReceivedChatMessageId
            && this.lastReadReceivedChatMessageId == matchDTO.lastReadReceivedChatMessageId
            && this.lastReadByChatMessageId == matchDTO.lastReadByChatMessageId
            && this.lastChatMessageId == matchDTO.lastChatMessageId
            && this.lastChatMessageBody == matchDTO.lastChatMessageBody
            && this.createdAt == matchDTO.createdAt
            && this.swipedName == matchDTO.swipedName
            && this.swipedProfilePhotoKey == matchDTO.swipedProfilePhotoKey
            && this.swipedDeleted == matchDTO.swipedDeleted) {
            return true
        }
        return false
    }
}