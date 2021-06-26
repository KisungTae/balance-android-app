package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.Location
import org.threeten.bp.OffsetDateTime

@Dao
interface LocationDAO {

    @Query("select * from location where id = ${Location.ID}")
    fun findById(): Location

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(location: Location)

    @Query("update location set synced = 1 where id = ${Location.ID} and updatedAt = :updatedAt")
    fun sync(updatedAt: OffsetDateTime)

    @Query("select count() > 0 from location where id = ${Location.ID}")
    fun exist(): Boolean

    @Query("delete from location")
    fun deleteAll()
}