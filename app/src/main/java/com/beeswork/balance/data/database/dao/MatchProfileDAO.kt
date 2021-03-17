package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.MatchProfile

@Dao
interface MatchProfileDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(matchProfile: MatchProfile)

    @Query("select * from matchProfile where id = ${MatchProfile.ID}")
    fun findById(): MatchProfile?
}