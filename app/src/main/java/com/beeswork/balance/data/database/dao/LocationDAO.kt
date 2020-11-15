package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.LOCATION_ID
import com.beeswork.balance.data.database.entity.Location
import org.threeten.bp.OffsetDateTime

@Dao
interface LocationDAO {

    @Query("select * from location where id = $LOCATION_ID")
    fun get(): Location

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(location: Location)

    @Query("update location set synced = 1 where id = $LOCATION_ID and updatedAt = :updatedAt")
    fun sync(updatedAt: OffsetDateTime)


}