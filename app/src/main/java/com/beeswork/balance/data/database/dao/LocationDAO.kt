package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.setting.Location
import com.beeswork.balance.internal.constant.LocationPermissionStatus
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.OffsetDateTime

@Dao
interface LocationDAO {

    @Query("select * from location where id = ${Location.ID}")
    fun getById(): Location?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(location: Location)

    @Query("update location set synced = :synced where id = ${Location.ID}")
    fun updateSyncedBy(synced: Boolean)

    @Query("update location set synced = 1 where id = ${Location.ID} and updatedAt = :updatedAt")
    fun updateAsSyncedBy(updatedAt: OffsetDateTime)

    @Query("select count() > 0 from location where id = ${Location.ID}")
    fun existById(): Boolean

    @Query("select * from location where id = ${Location.ID}")
    fun getLocationFlow(): Flow<Location?>

    @Query("select locationPermissionStatus from location where id = ${Location.ID}")
    fun getLocationPermissionStatusFlow(): Flow<LocationPermissionStatus?>

    @Query("update location set locationPermissionStatus = :locationPermissionStatus where id = ${Location.ID}")
    fun updateLocationPermissionStatus(locationPermissionStatus: LocationPermissionStatus): Int
}