package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.Setting
import com.beeswork.balance.data.database.tuple.PushSettingsTuple
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(setting: Setting)

    @Query("select * from setting where id = ${Setting.ID}")
    fun findById(): Setting

    @Query("select count() > 0 from setting where id = ${Setting.ID}")
    fun exist(): Boolean

    @Query("select email from setting where id = ${Setting.ID}")
    fun findEmailFlow(): Flow<String?>

    @Query("update setting set email = :email, emailSynced = 1 where id = ${Setting.ID}")
    fun syncEmail(email: String)

    @Query("update setting set emailSynced = :emailSynced where id = ${Setting.ID}")
    fun updateEmailSynced(emailSynced: Boolean)

    @Query("select emailSynced from setting where id = ${Setting.ID}")
    fun findEmailSynced(): Boolean

    @Query("update setting set matchPush = :matchPush, matchPushSynced = 0 where id = ${Setting.ID}")
    fun updateMatchPush(matchPush: Boolean)

    @Query("update setting set clickedPush = :clickedPush, clickedPushSynced = 0 where id = ${Setting.ID}")
    fun updateClickedPush(clickedPush: Boolean)

    @Query("update setting set chatMessagePush = :chatMessagePush, chatMessagePushSynced = 0 where id = ${Setting.ID}")
    fun updateChatMessagePush(chatMessagePush: Boolean)

    @Query("update setting set matchPushSynced = 1 where id = ${Setting.ID}")
    fun syncMatchPush()

    @Query("update setting set clickedPushSynced = 1 where id = ${Setting.ID}")
    fun syncClickedPush()

    @Query("update setting set chatMessagePushSynced = 1 where id = ${Setting.ID}")
    fun syncChatMessagePush()

    @Query("update setting set matchPushSynced = 1, matchPush = case when matchPush == 1 then 0 else 1 end where id = 0")
    fun revertMatchPush()

    @Query("update setting set clickedPushSynced = 1, clickedPush = case when clickedPush == 1 then 0 else 1 end where id = 0")
    fun revertClickedPush()

    @Query("update setting set chatMessagePushSynced = 1, chatMessagePush = case when chatMessagePush == 1 then 0 else 1 end where id = 0")
    fun revertChatMessagePush()

    @Query("select matchPush from setting where id = ${Setting.ID}")
    fun findMatchPush(): Boolean

    @Query("select clickedPush from setting where id = ${Setting.ID}")
    fun findClickedPush(): Boolean

    @Query("select chatMessagePush from setting where id = ${Setting.ID}")
    fun findChatMessagePush(): Boolean

    @Query("select matchPush, clickedPush, chatMessagePush from setting where id = ${Setting.ID}")
    fun findPushSettingsFlow(): Flow<PushSettingsTuple>
}