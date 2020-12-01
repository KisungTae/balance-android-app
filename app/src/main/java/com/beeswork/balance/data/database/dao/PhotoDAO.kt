package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.Photo

@Dao
interface PhotoDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(photo: Photo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(photos: List<Photo>)

    @Query("select * from photo")
    fun getPhotos(): List<Photo>

    @Query("update photo set synced = 1 where `key` = :key")
    fun sync(key: String)

    @Query("select exists (select * from photo where synced = :synced)")
    fun existsBySynced(synced: Boolean): Boolean

    @Query("delete from photo where `key` not in (:photoIds)")
    fun deletePhotosIn(photoIds: List<String>)
}
