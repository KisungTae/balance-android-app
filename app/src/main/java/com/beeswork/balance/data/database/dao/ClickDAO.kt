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

    @Query("delete from click where swiperId = :swiperId")
    fun deleteBySwiperId(swiperId: UUID)

    @Query("delete from click where swiperId in (:swiperIds)")
    fun deleteInSwiperIds(swiperIds: List<UUID>)

    @Query("delete from click where swiperId in (select swipedId from `match`)")
    fun deleteIfMatched()

    @Query("select count(*) from click")
    fun count(): Flow<Int>

    @Query("select * from click order by updatedAt desc limit :loadSize offset :startPosition ")
    fun findAllPaged(loadSize: Int, startPosition: Int): List<Click>

    @Query("select 1 from click")
    fun invalidation(): Flow<Boolean>

    @Query("delete from click")
    fun deleteAll()
}