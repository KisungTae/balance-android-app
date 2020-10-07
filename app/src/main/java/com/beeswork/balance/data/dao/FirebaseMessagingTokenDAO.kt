package com.beeswork.balance.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.entity.FirebaseMessagingToken

@Dao
interface FirebaseMessagingTokenDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(firebaseMessagingToken: FirebaseMessagingToken)

    @Query("update firebaseMessagingToken set posted = :posted where id = :id")
    fun updatePosted(id:Int, posted: Boolean)

    @Query("select * from firebaseMessagingToken")
    fun get(): List<FirebaseMessagingToken>

}