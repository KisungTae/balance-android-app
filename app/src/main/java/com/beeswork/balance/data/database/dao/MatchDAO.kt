package com.beeswork.balance.data.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.Match

@Dao
interface MatchDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(matches: List<Match>)

    @Query("select * from `match` where unmatched != 1 order by lastReceivedAt desc")
    fun getMatches(): DataSource.Factory<Int, Match>

    @Query("update `match` set unmatched = 1 where matchedId = :matchedId")
    fun unmatch(matchedId: String)

    @Query("select exists (select * from `match` where chatId = :chatId)")
    fun existsByChatId(chatId: Long): Boolean

    @Query("update `match` set photoKey = :photoKey, unmatched = :unmatched where chatId = :chatId")
    fun update(chatId: Long, photoKey: String, unmatched: Boolean)

    @Query("select matchedId from `match`")
    fun getMatchedIds(): List<String>

    @Query("select count(unreadMessageCount) from `match` where unmatched != 1")
    fun countUnreadMessageCount(): LiveData<Int>

//  TODO: removeme
    @Query("select * from `match`")
    fun get(): List<Match>

}