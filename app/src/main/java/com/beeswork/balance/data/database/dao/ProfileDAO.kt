package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.data.database.entity.Profile

@Dao
interface ProfileDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(profile: Profile)

    @Query("select * from profile where id = :id")
    fun findById(id: Int = Profile.ID): Profile?

    @Query("update profile set height = :height, about = :about, synced = 0 where id = ${Profile.ID}")
    fun updateAbout(height: Int?, about: String)

    @Query("update profile set synced = 1 where id = ${Profile.ID}")
    fun sync()


}