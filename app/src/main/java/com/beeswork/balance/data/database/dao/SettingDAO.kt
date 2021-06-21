package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.Setting
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(setting: Setting)

    @Query("select * from setting where id = ${Setting.ID}")
    fun findById(): Setting?

    @Query("select email from setting where id = ${Setting.ID}")
    fun findEmailFlow(): Flow<String?>

    @Query("update setting set email = :email, emailSynced = 1 where id = ${Setting.ID}")
    fun syncEmail(email: String)

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

}