package com.beeswork.balance.data.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.Click
import java.util.*


@Dao
interface ClickDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(clicks: List<Click>)

    @Query("delete from click where swiperId in (:swiperIds)")
    fun deleteInIds(swiperIds: List<UUID>)


    @Query("select * from click order by updatedAt desc")
    fun getClicks(): DataSource.Factory<Int, Click>

    @Query("delete from click where swiperId = :swiperId")
    fun deleteById(swiperId: UUID)

    @Query("delete from click where swiperId in (select swipedId from `match`)")
    fun deleteIfMatched()

    @Query("select count(*) from click")
    fun count(): LiveData<Int>

    @Query("select * from click")
    fun get(): List<Click>
}