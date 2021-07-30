package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.Location
import com.beeswork.balance.data.database.entity.PushSetting
import com.beeswork.balance.data.database.tuple.LocationTuple
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface PushSettingDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(pushSetting: PushSetting)

    @Query("select * from setting where accountId = :accountId")
    fun findByAccountId(accountId: UUID): PushSetting?

    @Query("select synced from setting where accountId = :accountId")
    fun isSynced(accountId: UUID): Boolean?

    @Query("delete from setting where accountId = :accountId")
    fun delete(accountId: UUID)
}