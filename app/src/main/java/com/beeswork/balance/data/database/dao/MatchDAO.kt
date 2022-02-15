package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.match.Match
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface MatchDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(match: Match)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(matches: List<Match>)

    @Query("select * from `match` where chatId = :chatId")
    fun findById(chatId: Long?): Match?

    @Query("select count(swipedId) > 0 from `match` where swiperId = :swiperId and swipedId = :swipedId")
    fun existBy(swiperId: UUID?, swipedId: UUID): Boolean

    @Query("select * from `match` where swiperId = :accountId and name like :searchKeyword order by updatedAt desc, chatId desc limit :loadSize offset :startPosition")
    fun findAllPaged(accountId: UUID?, loadSize: Int, startPosition: Int, searchKeyword: String): List<Match>

    @Query("select * from `match` where swiperId = :accountId order by updatedAt desc, chatId desc limit :loadSize offset :startPosition")
    fun findAllPaged(accountId: UUID?, loadSize: Int, startPosition: Int): List<Match>

    @Query("select unmatched from `match` where chatId = :chatId")
    fun findUnmatched(chatId: Long): Boolean

    @Query("delete from `match` where chatId = :chatId")
    fun delete(chatId: Long)

    @Query("select 1 from `match`")
    fun getPageInvalidation(): Flow<Boolean>

    @Query("update `match` set unmatched = 1, updatedAt = null, recentChatMessage = '', profilePhotoKey = null, active = 1 where chatId = :chatId")
    fun updateAsUnmatched(chatId: Long?)

    @Query("select count(unread) from `match` where swiperId = :accountId and unread = 1 or active = 0")
    fun countUnread(accountId: UUID?): Flow<Int>

    @Query("delete from `match` where swiperId = :accountId")
    fun deleteAll(accountId: UUID?)

}