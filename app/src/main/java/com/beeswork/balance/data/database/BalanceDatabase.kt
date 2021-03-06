package com.beeswork.balance.data.database

import android.content.Context
import androidx.room.*
import com.beeswork.balance.data.database.dao.*
import com.beeswork.balance.data.database.entity.*
import com.beeswork.balance.data.database.converter.ChatMessageStatusConverter
import com.beeswork.balance.data.database.converter.OffsetDateTimeConverter
import com.beeswork.balance.data.database.converter.ResourceStatusConverter
import com.beeswork.balance.data.database.converter.UUIDConverter


@Database(
    entities = [Match::class, ChatMessage::class, Clicked::class, FCMToken::class,
        Clicker::class, Profile::class, Location::class, Photo::class, FetchMatchesResult::class],
    version = 1
)
@TypeConverters(
    OffsetDateTimeConverter::class,
    ChatMessageStatusConverter::class,
    UUIDConverter::class,
    ResourceStatusConverter::class
)
abstract class BalanceDatabase : RoomDatabase() {

    abstract fun matchDAO(): MatchDAO
    abstract fun chatMessageDAO(): ChatMessageDAO
    abstract fun clickDAO(): ClickedDAO
    abstract fun fcmTokenDAO(): FCMTokenDAO
    abstract fun clickedDAO(): ClickerDAO
    abstract fun profileDAO(): ProfileDAO
    abstract fun locationDAO(): LocationDAO
    abstract fun photoDAO(): PhotoDAO
    abstract fun matchProfileDAO(): FetchMatchesResultDAO

    companion object {

        @Volatile
        private var instance: BalanceDatabase? = null
        private val lock = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(lock = lock) {
            instance ?: build(context).also { instance = it }
        }

        private fun build(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            BalanceDatabase::class.java,
            "balance.db"
        ).build()

    }
}


// list all indexes
// SELECT name, tbl_name  FROM sqlite_master  WHERE type = 'index';
