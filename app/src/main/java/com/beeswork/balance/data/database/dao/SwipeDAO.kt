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
    fun deleteBy(swiperId: UUID, swipedId: UUID?): Int

    @Query("select * from swipe where swipedId = :swiperId order by id desc limit :loadSize offset :startPosition ")
    fun findAllPaged(swiperId: UUID?, loadSize: Int, startPosition: Int): List<Swipe>

    @Query("delete from swipe where swipedId = :swiperId")
    fun deleteAll(swiperId: UUID?)

    @Query("select * from swipe where swiperId = :swiperId and swipedId = :swipedId")
    fun findBy(swiperId: UUID?, swipedId: UUID?): Swipe?

    @Query("select 1 from swipe")
    fun getPageInvalidationFlow(): Flow<Boolean>


}