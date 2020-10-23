package com.beeswork.balance.data.database.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.beeswork.balance.data.database.entity.Message

@Dao
interface MessageDAO {


    @Insert
    fun insert(message: Message)

    @Query("select * from message where chatId = :chatId order by createdAt desc")
    fun getMessages(chatId: Long): DataSource.Factory<Int, Message>

//  TODO: remove me
    @Query("update message set message = 'test message here'")
    fun updateMessages()
}