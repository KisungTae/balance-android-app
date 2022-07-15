package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.swipe.Swipe
import kotlinx.coroutines.flow.Flow
import java.util.*


@Dao
interface SwipeDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(swipe: Swipe)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(swipes: List<Swipe>)

    @Query("delete from swipe where swiperId = :swiperId and swipedId = :swipedId")
    fun delete(swiperId: UUID, swipedId: UUID?): Int

    @Query("delete from swipe where swipedId = :swipedId")
    fun deleteAll(swipedId: UUID?)

    @Query("delete from swipe where swiperId = :swiperId and id >= :fromSwipeId and id <= :toSwipeId")
    fun deleteBetween(swiperId: UUID?, fromSwipeId: Long, toSwipeId: Long)

    @Query("select * from swipe where swiperId = :swiperId and swipedId = :swipedId")
    fun get(swiperId: UUID?, swipedId: UUID?): Swipe?

    @Query("select 1 from swipe")
    fun getPageInvalidationFlow(): Flow<Boolean>

    @Query("select count(*) > 0 from swipe where swiperId = :swiperId and swipedId = :swipedId")
    fun exists(swiperId: UUID?, swipedId: UUID?): Boolean

    @Query("select * from swipe where swipedId = :swipedId and id <= :loadKey order by id desc limit :loadSize")
    fun getAppendedPageInclusive(swipedId: UUID?, loadKey: Long, loadSize: Int): List<Swipe>


}