package com.beeswork.balance.data.database.dao

import androidx.paging.DataSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.internal.constant.ChatMessageStatus
import org.threeten.bp.OffsetDateTime

@Dao
interface ChatMessageDAO {

    @Insert
    fun insert(chatMessage: ChatMessage): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chatMessages: List<ChatMessage>)

    //    @Query("select * from chatMessage where chatId = :chatId order by case when id is null then 0 else 1 end, id desc, messageId desc")
    @Query("select * from chatMessage where chatId = :chatId order by messageId desc")
    fun getChatMessages(chatId: Long): DataSource.Factory<Int, ChatMessage>

    @Query("update chatMessage set id = :id, createdAt = :createdAt, status = :status where chatId = :chatId and messageId = :messageId")
    fun sync(
        chatId: Long,
        messageId: Long,
        id: Long,
        createdAt: OffsetDateTime,
        status: ChatMessageStatus
    )

    @Query("select id from chatMessage where chatId = :chatId order by id desc limit 1")
    fun findLastId(chatId: Long): Long?

//    @Query("select createdAt from chatMessage where chatId = :chatId and status in (:statuses) order by id desc limit 1")
//    fun findLastCreatedAt(chatId: Long, statuses: Array<ChatMessageStatus> = arrayOf(ChatMessageStatus.SENT, ChatMessageStatus.RECEIVED))

    @Query("select * from chatMessage where chatId = :chatId and id > :firstChatMessageId order by id asc limit :pageSize")
    fun findAllAfter(chatId: Long, firstChatMessageId: Long, pageSize: Int): MutableList<ChatMessage>

    @Query("select * from chatMessage where chatId = :chatId and id < :lastChatMessageId order by id desc limit :pageSize")
    fun findAllBefore(chatId: Long, lastChatMessageId: Long, pageSize: Int): List<ChatMessage>

    @Query("select * from chatMessage where chatId = :chatId and messageId = null order by messageId desc")
    fun findAllUnprocessed(chatId: Long): List<ChatMessage>

    @Query("select * from chatMessage where chatId = :chatId order by id desc limit :pageSize")
    fun findAllRecent(chatId: Long, pageSize: Int): MutableList<ChatMessage>

    @Query("select count(*) from chatMessage where chatId = :chatId and id = null")
    fun countUnprocessed(chatId: Long): Int

    @Query("update chatMessage set status = :toStatus where chatId = :chatId and status = :fromStatus")
    fun updateStatus(chatId: Long, fromStatus: ChatMessageStatus, toStatus: ChatMessageStatus)

}