package com.beeswork.balance.data.database

import android.content.Context
import androidx.room.*
import com.beeswork.balance.data.dao.ClickDAO
import com.beeswork.balance.data.dao.FirebaseMessagingTokenDAO
import com.beeswork.balance.data.dao.MatchDAO
import com.beeswork.balance.data.dao.MessageDAO
import com.beeswork.balance.data.entity.Click
import com.beeswork.balance.data.entity.FirebaseMessagingToken
import com.beeswork.balance.data.entity.Match
import com.beeswork.balance.data.entity.Message
import com.beeswork.balance.internal.converter.OffsetDateTimeConverter


@Database(
    entities = [Match::class, Message::class, Click::class, FirebaseMessagingToken::class],
    version = 1
)
@TypeConverters(OffsetDateTimeConverter::class)
abstract class BalanceDatabase : RoomDatabase() {

    abstract fun matchDAO(): MatchDAO
    abstract fun messageDAO(): MessageDAO
    abstract fun failedClickDAO(): ClickDAO
    abstract fun firebaseMessagingTokenDAO(): FirebaseMessagingTokenDAO

    companion object {

        @Volatile
        private var instance: BalanceDatabase? = null
        private val lock = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(lock = lock) {
            instance ?: build(context).also { instance = it }
        }

        private fun build(context: Context) = Room.databaseBuilder(context.applicationContext,
                                                                   BalanceDatabase::class.java,
                                                                   "balance.db").build()

    }
}