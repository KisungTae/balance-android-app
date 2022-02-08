package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.setting.FCMToken

@Dao
interface FCMTokenDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(FCMToken: FCMToken)

    @Query("select token from fcmToken where id = ${FCMToken.ID}")
    fun findById(): String?
}