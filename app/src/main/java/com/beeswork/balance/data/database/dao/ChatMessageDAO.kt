package com.beeswork.balance.data.database.dao

import androidx.lifecycle.LiveData
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
    fun existById(id: Long): Boolean

    @Query("select * from chatMessage where `key` = :key")
    fun findByKey(key: Long): ChatMessage?

    @Query("select * from chatMessage where chatId = :chatId and id > :lastReadChatMessageId and status in (:statuses) order by id desc limit 1")
    fun findMostRecentAfter(
        chatId: Long,
        lastReadChatMessageId: Long,
        statuses: List<ChatMessageStatus> = listOf(ChatMessageStatus.SENT, ChatMessageStatus.RECEIVED)
    ): ChatMessage?

    @Query("select count(*) > 0 from chatMessage where chatId = :chatId and status = :status and id > :lastReadChatMessageId limit 1")
    fun existByIdGreaterThan(
        chatId: Long,
        lastReadChatMessageId: Long,
        status: ChatMessageStatus = ChatMessageStatus.RECEIVED
    ): Boolean

    @Query("select * from chatMessage where chatId = :chatId order by id desc, `key` desc limit :loadSize offset :startPosition")
    fun findAllPaged(loadSize: Int, startPosition: Int, chatId: Long): List<ChatMessage>

    @Query("select body from chatMessage where id = :id")
    fun findBodyById(id: Long): String?

    @Query("select unmatched = 0 from `match` where chatId = :chatId")
    fun findUnmatchedById(chatId: Long): LiveData<Boolean>

    @Query("update chatMessage set status = :status where `key` = :key")
    fun updateStatus(key: Long, status: ChatMessageStatus)

    @Query("update chatMessage set status = :toStatus where status = :status and createdAt < :createdAt")
    fun updateStatusByStatusAndCreatedAt(toStatus: ChatMessageStatus, status: ChatMessageStatus, createdAt: OffsetDateTime)

    @Query("delete from chatMessage where `key` = :key")
    fun delete(key: Long)
}