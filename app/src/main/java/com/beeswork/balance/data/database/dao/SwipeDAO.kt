package com.beeswork.balance.data.database.dao

import androidx.room.*
import com.beeswork.balance.data.database.entity.Swipe
import java.util.*

@Dao
interface SwipeDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(swipe: Swipe)

    @Query("select count(*) > 0 from swipe where swiperId = :accountId and swipedId = :swipedId")
    fun existBySwipedId(accountId: UUID?, swipedId: UUID): Boolean

    @Query("delete from swipe where swiperId = :accountId")
    fun deleteAll(accountId: UUID?)
}