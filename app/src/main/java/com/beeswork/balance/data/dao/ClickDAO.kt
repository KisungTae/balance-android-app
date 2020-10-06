package com.beeswork.balance.data.dao

import androidx.room.*
import com.beeswork.balance.data.entity.Click

@Dao
interface ClickDAO {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(click: Click)

    @Query("update click set posted = :posted where swipeId = :swipeId")
    fun updatePosted(swipeId: Long, posted: Boolean)

    @Query("delete from click where swipeId = :swipeId")
    fun delete(swipeId: Long)

    @Query("select swipedId from click")
    fun getSwipedIds(): List<String>
}