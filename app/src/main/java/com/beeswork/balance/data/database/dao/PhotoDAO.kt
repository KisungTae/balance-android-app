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

    @Query("select * from photo order by sequence")
    fun findAll(): List<Photo>

    @Query("update photo set synced = :synced where `key` = :photoKey")
    fun sync(photoKey: String, synced: Boolean)

    @Query("select exists (select * from photo where synced = :synced)")
    fun existsBySynced(synced: Boolean): Boolean

    @Query("select count(*) from photo")
    fun count(): Int

    @Query("delete from photo where `key` not in (:photoIds)")
    fun deletePhotosNotIn(photoIds: List<String>)

    @Query("delete from photo where `key` = :photoKey")
    fun deletePhoto(photoKey: String)

    @Query("update photo set sequence = :sequence where `key` = :photoKey")
    fun updateSequence(photoKey: String, sequence: Int)

    @Query("select `key` from photo order by sequence limit 1")
    fun findFirstPhotoKey(): String?
}
