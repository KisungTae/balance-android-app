package com.beeswork.balance.data.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.Match
import org.threeten.bp.OffsetDateTime
import java.util.*

@Dao
interface MatchDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(match: Match)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(matches: List<Match>)

    @Query("select * from `match` where chatId = :chatId")
    fun findById(chatId: Long): Match?

    @Query("select * from `match` where chatId = :chatId and deleted = 0 and unmatched = 0")
    fun findValidById(chatId: Long): Match?

    @Query("select * from `match` where name like :searchKeyword order by updatedAt desc, chatId desc limit :loadSize offset :startPosition")
    fun findAllPaged(loadSize: Int, startPosition: Int, searchKeyword: String): List<Match>

    @Query("select * from `match` order by updatedAt desc, chatId desc limit :loadSize offset :startPosition")
    fun findAllPaged(loadSize: Int, startPosition: Int): List<Match>


}