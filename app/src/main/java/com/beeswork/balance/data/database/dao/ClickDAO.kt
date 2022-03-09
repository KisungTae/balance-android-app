package com.beeswork.balance.data.database.dao

import androidx.room.*
import com.beeswork.balance.data.database.entity.swipe.Click
import java.util.*

@Dao
interface ClickDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(click: Click)

    @Query("select count(*) > 0 from click where swiperId = :swiperId and swipedId = :swipedId")
    fun existBy(swiperId: UUID?, swipedId: UUID): Boolean

    @Query("delete from click where swiperId = :accountId")
    fun deleteAllBy(accountId: UUID?)
}