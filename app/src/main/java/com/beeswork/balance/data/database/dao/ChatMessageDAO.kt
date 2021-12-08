package com.beeswork.balance.data.database.dao

import androidx.room.*
import com.beeswork.balance.data.database.entity.chat.ChatMessage
import com.beeswork.balance.data.database.entity.chat.ChatMessageToSendTuple
import com.beeswork.balance.internal.constant.ChatMessageStatus
import org.threeten.bp.OffsetDateTime
import java.util.*

@Dao
interface ChatMessageDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chatMessage: ChatMessage): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chatMessages: List<ChatMessage>)

    @Query("select * from chatMessage where `key` = :key")
    fun findByKey(key: Long?): ChatMessage?

    @Query("select * from chatMessage where chatId = :chatId and id > :lastReadChatMessageId and status in (:statuses) order by id desc limit 1")
    fun findMostRecentAfter(
        chatId: Long,
        lastReadChatMessageId: Long,
        statuses: List<ChatMessageStatus> = listOf(ChatMessageStatus.SENT, ChatMessageStatus.RECEIVED)
    ): ChatMessage?

    @Query("select count(*) > 0 from chatMessage where chatId = :chatId and status = :status and id > :lastReadChatMessageId limit 1")
    fun existAfter(
        chatId: Long,
        lastReadChatMessageId: Long,
        status: ChatMessageStatus = ChatMessageStatus.RECEIVED
    ): Boolean

    @Query("select * from chatMessage where chatId = :chatId order by id desc, `key` desc limit :loadSize offset :startPosition")
    fun findAllPaged(loadSize: Int, startPosition: Int, chatId: Long): List<ChatMessage>

    @Query("update chatMessage set status = :status where `key` = :key")
    fun updateStatusByKey(key: Long?, status: ChatMessageStatus)

    @Query("update chatMessage set status = :status where `key` in (:keys)")
    fun updateStatusByKeys(keys: List<Long>, status: ChatMessageStatus)

    @Query("update chatMessage set status = :toStatus where status = :whereStatus and createdAt < :createdAt")
    fun updateStatusBefore(createdAt: OffsetDateTime, whereStatus: ChatMessageStatus, toStatus: ChatMessageStatus)

    @Query("delete from chatMessage where `key` = :key")
    fun deleteByKey(key: Long)

    @Query("delete from chatMessage where chatId = :chatId")
    fun deleteByChatId(chatId: Long)

    @Query("delete from chatMessage")
    fun deleteAll()

    @Query("select chatId from chatMessage where `key` = :key")
    fun findChatIdByKey(key: Long?): Long?

//    @Query("select swipedId from `match` m left join chatMessage c on m.chatId == c.chatId where c.`key` = :key")
//    fun findSwipedIdByKey(key: Long?): UUID?

    @Query("select c.`key`, m.chatId, c.body, m.swipedId from `match` m left join chatMessage c on m.chatId == c.chatId where c.`key` = :key")
    fun findChatMessageToSendTupleByKey(key: Long?): ChatMessageToSendTuple?

}