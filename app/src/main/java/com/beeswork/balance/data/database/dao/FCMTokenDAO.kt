package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.FCMToken
import com.beeswork.balance.data.database.entity.FCM_TOKEN_ID

@Dao
interface FCMTokenDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(FCMToken: FCMToken)

    @Query("update fcmToken set posted = 1 where id = $FCM_TOKEN_ID")
    fun sync()

    @Query("select * from fcmToken")
    fun get(): List<FCMToken>

}