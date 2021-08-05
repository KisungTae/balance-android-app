package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.FCMToken
import kotlinx.coroutines.flow.Flow

@Dao
interface FCMTokenDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(FCMToken: FCMToken)

    @Query("update fcmToken set posted = 1 where id = ${FCMToken.ID}")
    fun sync()

    @Query("select active from fcmToken where id = ${FCMToken.ID}")
    fun findActiveAsFlow(): Flow<Boolean>

    @Query("update fcmToken set active = :active where id = ${FCMToken.ID}")
    fun updateActive(active: Boolean)
}