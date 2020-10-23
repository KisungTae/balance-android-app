package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.FCMToken

@Dao
interface FCMTokenDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(FCMToken: FCMToken)

    @Query("update fcmToken set posted = :posted where id = :id")
    fun updatePosted(id:Int, posted: Boolean)

    @Query("select * from fcmToken")
    fun get(): List<FCMToken>

}