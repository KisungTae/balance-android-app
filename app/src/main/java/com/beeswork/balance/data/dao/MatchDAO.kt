package com.beeswork.balance.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.entity.Match
import com.beeswork.balance.data.entity.Message

@Dao
interface MatchDAO {

    @Insert
    fun insert(match: Match)

    @Query("update `match` set unmatched = 1 where matchedId = :matchedId")
    fun unmatch(matchedId: String)

    @Query("select * from `match` order by date(updatedAt) desc")
    fun getMatches(): LiveData<List<Match>>

    @Query("select matchedId from `match`")
    fun getMatchedIds(): List<String>
}