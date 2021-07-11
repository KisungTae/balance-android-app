package com.beeswork.balance.data.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.Click
import kotlinx.coroutines.flow.Flow
import java.util.*


@Dao
interface ClickDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(click: Click)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(clicks: List<Click>)

    @Query("delete from click where swipedId = :accountId and swiperId = :swiperId")
    fun deleteBySwiperId(accountId: UUID?, swiperId: UUID)

    @Query("select count(*) from click where swipedId = :accountId")
    fun count(accountId: UUID?): Flow<Int>

    @Query("select * from click where swipedId = :accountId order by updatedAt desc limit :loadSize offset :startPosition ")
    fun findAllPaged(accountId: UUID?, loadSize: Int, startPosition: Int): List<Click>

    @Query("select 1 from click")
    fun invalidation(): Flow<Boolean>

    @Query("delete from click where swipedId = :accountId")
    fun deleteAll(accountId: UUID?)
}