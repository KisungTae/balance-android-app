package com.beeswork.balance.data.database.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.beeswork.balance.data.database.entity.Message
import org.threeten.bp.OffsetDateTime

@Dao
interface MessageDAO {


    @Insert
    fun insert(message: Message): Long

    @Query("select * from message where chatId = :chatId order by case when createdAt is null then 0 else 1 end, createdAt desc, messageId desc")
    fun getMessages(chatId: Long): DataSource.Factory<Int, Message>

    @Query("update message set id = :id, createdAt = :createdAt, status = :status where chatId = :chatId and messageId = :messageId")
    fun sync(chatId: Long, messageId: Long, id: Long, createdAt: OffsetDateTime, status: Message.Status)

    @Query("select * from message where chatId = :chatId and messageId = :messageId")
    fun getMessage(chatId: Long, messageId: Long): Message?

    @Query("select id from message order by id desc limit 1")
    fun getLastId(): Long
}