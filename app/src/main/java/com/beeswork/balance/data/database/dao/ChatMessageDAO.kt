package com.beeswork.balance.data.database.dao

import androidx.paging.DataSource
import androidx.room.*
import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.internal.constant.ChatMessageStatus
import org.threeten.bp.OffsetDateTime

@Dao
interface ChatMessageDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chatMessage: ChatMessage): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chatMessages: List<ChatMessage>)

    @Query("select count(*) > 0 from chatMessage where id = :id")
    fun existsById(id: Long): Boolean

    @Query("select * from chatMessage where `key` = :key")
    fun findByKey(key: Long): ChatMessage?

    @Query(
        """
        select * 
        from chatMessage 
        where chatId = :chatId 
        and id > :lastReadChatMessageId 
        and status in (:statuses) 
        order by id desc 
        limit 1
    """
    )
    fun findMostRecentAfter(
        chatId: Long,
        lastReadChatMessageId: Long,
        statuses: List<ChatMessageStatus> = listOf(ChatMessageStatus.SENT, ChatMessageStatus.RECEIVED)
    ): ChatMessage?

    @Query("select count(*) > 0 from chatMessage where chatId = :chatId and status = :status and id > :lastReadChatMessageId limit 1")
    fun unreadExists(
        chatId: Long,
        lastReadChatMessageId: Long,
        status: ChatMessageStatus = ChatMessageStatus.RECEIVED
    ): Boolean

    @Query("select * from chatMessage where chatId = :chatId order by id desc, `key` desc limit :loadSize offset :startPosition")
    fun findAllPaged(loadSize: Int, startPosition: Int, chatId: Long): List<ChatMessage>

    @Query("select body from chatMessage where id = :chatMessageId")
    fun findBodyById(chatMessageId: Long): String?

}