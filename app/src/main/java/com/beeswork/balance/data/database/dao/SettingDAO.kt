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
}