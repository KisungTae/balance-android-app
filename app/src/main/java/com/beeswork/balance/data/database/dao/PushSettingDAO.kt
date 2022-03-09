package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.setting.PushSetting
import java.util.*

@Dao
interface PushSettingDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(pushSetting: PushSetting)

    @Query("select * from pushSetting where accountId = :accountId")
    fun getBy(accountId: UUID?): PushSetting?

    @Query("select synced from pushSetting where accountId = :accountId")
    fun isSyncedBy(accountId: UUID): Boolean?

    @Query("delete from pushSetting where accountId = :accountId")
    fun deleteBy(accountId: UUID?)

    @Query("update pushSetting set synced = :synced where accountId = :accountId")
    fun updateSyncedBy(accountId: UUID?, synced: Boolean)

    @Query("update pushSetting set matchPush = :matchPush, swipePush = :swipePush, chatMessagePush = :chatMessagePush, emailPush = :emailPush, synced = 1 where accountId = :accountId")
    fun updatePushSettingsBy(
        accountId: UUID,
        matchPush: Boolean,
        swipePush: Boolean,
        chatMessagePush: Boolean,
        emailPush: Boolean
    )
}