package com.beeswork.balance.data.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.Match
import java.util.*

@Dao
interface MatchDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(match: Match)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(matches: List<Match>)

    @Query("select * from `match` where chatId = :chatId")
    fun findById(chatId: Long): Match?

    @Query("select * from `match` order by updatedAt desc")
    fun findAllPaged(): DataSource.Factory<Int, Match>











    @Query("update `match` set unmatched = 1 where matchedId = :matchedId")
    fun unmatch(matchedId: String)

    @Query("select exists (select * from `match` where chatId = :chatId)")
    fun existsByChatId(chatId: Long): Boolean

    @Query("select unmatched from `match` where chatId = :chatId")
    fun isUnmatched(chatId: Long): Boolean

    @Query("update `match` set repPhotoKey = :repPhotoKey, unmatched = :unmatched where chatId = :chatId")
    fun update(chatId: Long, repPhotoKey: String, unmatched: Boolean)

    @Query("select matchedId from `match`")
    fun getMatchedIds(): List<UUID>

    @Query("select count(name) from `match` where unmatched != 1")
    fun countUnreadMessageCount(): LiveData<Int>

    //  TODO: removeme
    @Query("select * from `match`")
    fun get(): List<Match>

}