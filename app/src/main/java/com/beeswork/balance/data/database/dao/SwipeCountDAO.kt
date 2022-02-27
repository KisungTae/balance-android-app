package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.swipe.SwipeCount
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.OffsetDateTime
import java.util.*

@Dao
interface SwipeCountDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(swipeCount: SwipeCount)

    @Query("select * from swipeCount where accountId = :accountId")
    fun findBy(accountId: UUID?): SwipeCount?

    @Query("select count from swipeCount where accountId = :accountId")
    fun getCountFlow(accountId: UUID?): Flow<Long?>

    @Query("select count from swipeCount where accountId = :accountId")
    fun getCountBy(accountId: UUID?): Long?

    @Query("delete from swipeCount where accountId = :accountId")
    fun deleteBy(accountId: UUID?)
}