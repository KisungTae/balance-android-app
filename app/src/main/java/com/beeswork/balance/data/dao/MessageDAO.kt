package com.beeswork.balance.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.entity.Message

@Dao
interface MessageDAO {


    @Insert
    fun insert(message: Message)

    @Query("select * from message where matchId = :matchId order by createdAt desc")
    fun getMessages(matchId: Int): DataSource.Factory<Int, Message>
}