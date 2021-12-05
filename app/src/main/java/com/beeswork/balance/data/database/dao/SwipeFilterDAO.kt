package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.swipe.SwipeFilter
import java.util.*

@Dao
interface SwipeFilterDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(swipeFilter: SwipeFilter)

    @Query("select * from swipeFilter where accountId = :accountId")
    fun findById(accountId: UUID?): SwipeFilter

    @Query("update swipeFilter set pageIndex = :pageIndex where accountId = :accountId")
    fun updatePageIndex(accountId: UUID?, pageIndex: Int)

    @Query("select count() > 0 from swipeFilter where accountId = :accountId")
    fun existByAccountId(accountId: UUID?): Boolean

    @Query("delete from swipeFilter where accountId = :accountId")
    fun deleteAll(accountId: UUID?)

    @Query("update swipeFilter set gender = :gender, minAge = :minAge, maxAge = :maxAge, distance = :distance where accountId = :accountId")
    fun update(accountId: UUID?, gender: Boolean, minAge: Int, maxAge: Int, distance: Int)
}