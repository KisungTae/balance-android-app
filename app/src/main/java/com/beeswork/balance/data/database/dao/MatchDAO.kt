package com.beeswork.balance.data.database.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.Match

@Dao
interface MatchDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMatches(matches: List<Match>)

    @Query("select * from `match` order by updatedAt desc")
    fun getMatches(): DataSource.Factory<Int, Match>

    @Query("update `match` set unmatched = 1 where matchedId = :matchedId")
    fun unmatch(matchedId: String)

    @Query("select exists (select * from `match` where chatId = :chatId)")
    fun existsByChatId(chatId: Long): Boolean

    @Query("update `match` set photoKey = :photoKey, unmatched = :unmatched where chatId = :chatId")
    fun updatePhotoKeyAndUnmatched(chatId: Long, photoKey: String, unmatched: Boolean)

    @Query("select matchedId from `match`")
    fun getMatchedIds(): List<String>

}