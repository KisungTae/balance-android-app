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

    @Query("select count(swipedId) > 0 from `match` where swiperId = :swiperId and swipedId = :swipedId")
    fun existBy(swiperId: UUID?, swipedId: UUID): Boolean

    @Query("select * from `match` where chatId = :chatId")
    fun findByChatId(chatId: UUID?): Match?

    @Query("select * from `match` where swiperId = :swiperId and lastChatMessageId = 0 and unmatched = 0 and swipedDeleted = 0 order by id desc limit :loadSize offset :startPosition")
    fun findMatchesPaged(swiperId: UUID?, loadSize: Int, startPosition: Int): List<Match>

    @Query("select * from `match` where swiperId = :swiperId and lastChatMessageId > 0 and unmatched = 0 and swipedDeleted = 0  order by id desc limit :loadSize offset :startPosition")
    fun findChatsPaged(swiperId: UUID?, loadSize: Int, startPosition: Int): List<Match>

    @Query("select * from `match` where swiperId = :swiperId and lastReadChatMessageId < lastChatMessageId and unmatched = 0 and swipedDeleted = 0 order by id desc limit :loadSize offset :startPosition")
    fun findChatsWithMessagesPaged(swiperId: UUID?, loadSize: Int, startPosition: Int): List<Match>

    @Query("select * from `match` where swiperId = :swiperId order by id desc limit :loadSize offset :startPosition")
    fun findAllPaged(swiperId: UUID?, loadSize: Int, startPosition: Int): List<Match>

    @Query("select unmatched from `match` where chatId = :chatId")
    fun isUnmatched(chatId: Long): Boolean

    @Query("delete from `match` where chatId = :chatId")
    fun deleteBy(chatId: Long)

    @Query("select 1 from `match`")
    fun getPageInvalidationFlow(): Flow<Boolean>

    @Query("delete from `match` where swiperId = :swiperId")
    fun deleteAll(swiperId: UUID?)

}