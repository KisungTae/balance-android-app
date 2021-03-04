package com.beeswork.balance.data.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.Clicker
import java.util.*


@Dao
interface ClickerDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(clickers: List<Clicker>)

    @Query("select * from clicker order by updatedAt desc")
    fun getClickers(): DataSource.Factory<Int, Clicker>

    @Query("delete from clicker where id = :clickerId")
    fun deleteById(clickerId: UUID)

    @Query("delete from clicker where id in (select matchedId from `match`)")
    fun deleteIfMatched()

    @Query("select count(*) from clicker")
    fun count(): LiveData<Int>

    @Query("select * from clicker")
    fun get(): List<Clicker>
}