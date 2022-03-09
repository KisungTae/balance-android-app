package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.profile.Profile
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface ProfileDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(profile: Profile)

    @Query("select * from profile where accountId = :accountId")
    fun getBy(accountId: UUID?): Profile?

    @Query("update profile set height = :height, about = :about, synced = 1 where accountId = :accountId")
    fun updateAboutBy(accountId: UUID, height: Int?, about: String)

    @Query("delete from profile where accountId = :accountId")
    fun deleteBy(accountId: UUID?)

    @Query("select name from profile where accountId = :accountId")
    fun getNameFlowBy(accountId: UUID?): Flow<String?>

    @Query("update profile set synced = :synced where accountId = :accountId")
    fun updateSyncedBy(accountId: UUID, synced: Boolean)
}