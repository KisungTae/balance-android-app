package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FetchMatchesResultDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(fetchMatchesResult: FetchMatchesResult)

    @Query("select * from fetchMatchesResult where id = ${FetchMatchesResult.ID}")
    fun findById(): FetchMatchesResult?

}