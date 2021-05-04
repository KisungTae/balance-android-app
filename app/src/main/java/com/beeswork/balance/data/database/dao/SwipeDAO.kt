package com.beeswork.balance.data.database.dao

import androidx.room.*
import com.beeswork.balance.data.database.entity.Swipe
import java.util.*

@Dao
interface SwipeDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(swipe: Swipe)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(swipeList: List<Swipe>)

    @Query("select swipedId from swipe")
    fun findSwipedIds(): List<UUID>

//  TODO: remove me
    @Query("select * from swipe")
    fun get(): List<Swipe>

}