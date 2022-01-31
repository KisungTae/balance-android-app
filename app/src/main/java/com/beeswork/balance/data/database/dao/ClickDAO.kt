package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.click.Click
import kotlinx.coroutines.flow.Flow
import java.util.*


@Dao
interface ClickDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(click: Click)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(clicks: List<Click>)

    @Query("delete from click where swiperId = :swiperId and swipedId = :swipedId")
    fun deleteBySwiperId(swiperId: UUID, swipedId: UUID?)

    @Query("delete from click where swiperId = :swiperId")
    fun deleteBySwiperId(swiperId: UUID)

    @Query("select count(*) from click where swipedId = :accountId")
    fun count(accountId: UUID?): Flow<Int>

    @Query("select * from click where swipedId = :accountId order by id desc limit :loadSize offset :startPosition ")
    fun findAllPaged(accountId: UUID?, loadSize: Int, startPosition: Int): List<Click>

    @Query("delete from click where swipedId = :accountId")
    fun deleteAll(accountId: UUID?)

    @Query("select 1 from click")
    fun findInvalidation(): Flow<Boolean>
}