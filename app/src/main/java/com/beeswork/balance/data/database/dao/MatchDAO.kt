package com.beeswork.balance.data.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.internal.constant.ChatMessageStatus
import org.threeten.bp.OffsetDateTime
import java.util.*

@Dao
interface MatchDAO {

    @Query("select * from `match` where chatId = :chatId")
    fun findById(chatId: Long): Match?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(matches: List<Match>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(match: Match)

    @Query("select * from `match` order by lastReadChatMessageId desc")
    fun getMatches(): DataSource.Factory<Int, Match>

    @Query("update `match` set unmatched = 1 where matchedId = :matchedId")
    fun unmatch(matchedId: String)

    @Query("select exists (select * from `match` where chatId = :chatId)")
    fun existsByChatId(chatId: Long): Boolean

    @Query("select unmatched from `match` where chatId = :chatId")
    fun isUnmatched(chatId: Long): Boolean

    @Query("update `match` set repPhotoKey = :repPhotoKey, unmatched = :unmatched where chatId = :chatId")
    fun update(chatId: Long, repPhotoKey: String, unmatched: Boolean)

    @Query("select matchedId from `match`")
    fun getMatchedIds(): List<UUID>

    @Query("select count(name) from `match` where unmatched != 1")
    fun countUnreadMessageCount(): LiveData<Int>

    //  TODO: removeme
    @Query("select * from `match`")
    fun get(): List<Match>

    @Query(
        """
        update `match` 
        set unmatched = :unmatched 
        and updatedAt = :updatedAt 
        and name = :name 
        and repPhotoKey = :repPhotoKey 
        and blocked = :blocked 
        and deleted = :deleted 
        and accountUpdatedAt = :accountUpdatedAt
        and unreadMessageCount = :unreadMessageCount
        and recentMessage = :recentMessage
        where chatId = :chatId"""
    )
    fun updateMatch(
        chatId: Long,
        unmatched: Boolean,
        updatedAt: OffsetDateTime,
        name: String,
        repPhotoKey: String,
        blocked: Boolean,
        deleted: Boolean,
        accountUpdatedAt: OffsetDateTime,
        unreadMessageCount: Int,
        recentMessage: String
    )

    @Query(
        """
        update `match`
        set unreadMessageCount = (select count(cm.id)
                                    from chatMessage cm
                                    left join `match` m on cm.chatId = m.chatId
                                    where cm.chatId = :chatId
                                    and cm.id > m.lastReadChatMessageId
                                    and cm.status = :chatMessageStatus)
    """
    )
    fun updateUnreadMessageCount(
        chatId: Long,
        chatMessageStatus: ChatMessageStatus = ChatMessageStatus.RECEIVED
    )

    @Query(
        """
            update `match`
            set recentMessage = :recentMessage
            and updatedAt = :updatedAt
            where chatId = :chatId
        """
    )
    fun updateRecentMessage(
        chatId: Long,
        recentMessage: String,
        updatedAt: OffsetDateTime
    )

    @Query("select lastReadChatMessageId from `match` where chatId = :chatId")
    fun findLastReadChatMessageId(chatId: Long): Long?


//  TODO: remove me
    @Query("select * from `match`")
    fun findAll(): List<Match>

}