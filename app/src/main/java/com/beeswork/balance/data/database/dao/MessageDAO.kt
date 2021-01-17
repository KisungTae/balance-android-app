package com.beeswork.balance.data.database.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.beeswork.balance.data.database.entity.Message

@Dao
interface MessageDAO {


    @Insert
    fun insert(message: Message): Long

    @Query("select * from message where chatId = :chatId order by CASE WHEN createdAt IS NULL THEN 0 ELSE 1 END, createdAt DESC")
    fun getMessages(chatId: Long): DataSource.Factory<Int, Message>
}