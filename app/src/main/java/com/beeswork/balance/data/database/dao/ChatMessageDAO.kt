package com.beeswork.balance.data.database.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.beeswork.balance.data.database.entity.ChatMessage
import org.threeten.bp.OffsetDateTime

@Dao
interface ChatMessageDAO {

    @Insert
    fun insert(chatMessage: ChatMessage): Long

//    @Query("select * from chatMessage where chatId = :chatId order by case when createdAt is null then 0 else 1 end, createdAt asc, messageId asc")
    @Query("select * from chatMessage where chatId = :chatId order by messageId desc")
    fun getChatMessages(chatId: Long): DataSource.Factory<Int, ChatMessage>

    @Query("update chatMessage set id = :id, createdAt = :createdAt, status = :status where chatId = :chatId and messageId = :messageId")
    fun sync(
        chatId: Long,
        messageId: Long,
        id: Long,
        createdAt: OffsetDateTime,
        status: ChatMessage.Status
    )

    @Query("select id from chatMessage order by id desc limit 1")
    fun getLastId(): Long


    fun getChatMessages()
}