package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.profile.Profile
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.OffsetDateTime
import java.util.*

@Dao
interface ProfileDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(profile: Profile)

    @Query("select * from profile where accountId = :accountId")
    fun getBy(accountId: UUID?): Profile?

    @Query("update profile set height = :height, about = :about, synced = 1 where accountId = :accountId")
    fun updateAboutBy(accountId: UUID, height: Int?, about: String)

    @Query("delete from profile where accountId = :accountId")
    fun deleteBy(accountId: UUID?)

    @Query("select name from profile where accountId = :accountId")
    fun getNameFlowBy(accountId: UUID?): Flow<String?>

    @Query("update profile set synced = :synced where accountId = :accountId")
    fun updateSyncedBy(accountId: UUID, synced: Boolean)

    @Query("update profile set name = :name where accountId = :accountId")
    fun saveNameBy(accountId: UUID?, name: String)

    @Query("update profile set birthDate = :birthDate where accountId = :accountId")
    fun saveBirthDateBy(accountId: UUID?, birthDate: OffsetDateTime)

    @Query("update profile set gender = :gender where accountId = :accountId")
    fun saveGenderBy(accountId: UUID?, gender: Boolean)

    @Query("update profile set height = :height where accountId = :accountId")
    fun saveHeightBy(accountId: UUID?, height: Int?)

    @Query("update profile set about = :about where accountId = :accountId")
    fun saveAboutBy(accountId: UUID?, about: String)

    @Query("select name from profile where accountId = :accountId")
    fun getName(accountId: UUID?): String?

    @Query("select birthDate from profile where accountId = :accountId")
    fun getBirthDate(accountId: UUID?): OffsetDateTime?

    @Query("select gender from profile where accountId = :accountId")
    fun getGender(accountId: UUID?): Boolean?

    @Query("select height from profile where accountId = :accountId")
    fun getHeight(accountId: UUID?): Int?

    @Query("select about from profile where accountId = :accountId")
    fun getAbout(accountId: UUID?): String?

    @Query("select count(*) > 0 from profile where accountId = :accountId")
    fun existsBy(accountId: UUID?): Boolean
}