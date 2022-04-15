package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.photo.Photo
import com.beeswork.balance.internal.constant.PhotoStatus
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface PhotoDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(photo: Photo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(photos: List<Photo>)

    @Query("select * from photo where accountId = :accountId order by sequence limit :maxPhotoCount")
    fun getPhotoFlowBy(accountId: UUID?, maxPhotoCount: Int): Flow<List<Photo>>

    @Query("select sequence from photo where accountId = :accountId order by sequence desc limit 1")
    fun getLastSequenceBy(accountId: UUID?): Int?

    @Query("select * from photo where accountId = :accountId order by sequence limit :maxPhotoCount")
    fun getAllBy(accountId: UUID?, maxPhotoCount: Int): List<Photo>

    @Query("select * from photo where `key` = :key")
    fun getBy(key: String?): Photo?

    @Query("update photo set status = :status where `key` = :key")
    fun updateStatusBy(key: String, status: PhotoStatus)

    @Query("update photo set uploaded = :uploaded where `key` = :key")
    fun updateUploadedBy(key: String, uploaded: Boolean)

    @Query("update photo set status = :status, saved = :saved where `key` = :key")
    fun updateAsSavedBy(key: String, status: PhotoStatus = PhotoStatus.OCCUPIED, saved: Boolean = true)

    @Query("delete from photo where `key` = :photoKey")
    fun deleteBy(photoKey: String)

    @Query("select count(*) from photo where accountId = :accountId")
    fun getCountBy(accountId: UUID): Int

    @Query("update photo set sequence = :sequence where `key` = :photoKey")
    fun updateSequenceBy(photoKey: String, sequence: Int)

    @Query("select `key` from photo where accountId = :accountId order by sequence limit 1")
    fun getProfilePhotoKeyFlowBy(accountId: UUID?): Flow<String?>

    @Query("select * from photo where accountId = :accountId order by sequence limit 1")
    fun getProfilePhotoBy(accountId: UUID?): Photo?

    @Query("select `key` from photo where accountId = :accountId order by sequence limit 1")
    fun getProfilePhotoKeyBy(accountId: UUID?): String?

}
