package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.PushSetting
import java.util.*

@Dao
interface PushSettingDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(pushSetting: PushSetting)

    @Query("select * from pushSetting where accountId = :accountId")
    fun findByAccountId(accountId: UUID): PushSetting?

    @Query("select synced from pushSetting where accountId = :accountId")
    fun isSynced(accountId: UUID): Boolean?

    @Query("delete from pushSetting where accountId = :accountId")
    fun delete(accountId: UUID)

    @Query("update pushSetting set synced = :synced where accountId = :accountId")
    fun updateSynced(accountId: UUID, synced: Boolean)

    @Query("update pushSetting set matchPush = :matchPush, clickedPush = :clickedPush, chatMessagePush = :chatMessagePush, emailPush = :emailPush, synced = 1 where accountId = :accountId")
    fun updatePushSettings(
        accountId: UUID,
        matchPush: Boolean,
        clickedPush: Boolean,
        chatMessagePush: Boolean,
        emailPush: Boolean
    )
}