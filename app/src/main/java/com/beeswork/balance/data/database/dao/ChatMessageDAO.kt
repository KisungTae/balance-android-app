package com.beeswork.balance.data.database.dao

import androidx.room.*
import com.beeswork.balance.data.database.entity.chat.ChatMessage
import com.beeswork.balance.internal.constant.ChatMessageStatus
import org.threeten.bp.OffsetDateTime
import java.util.*

@Dao
interface ChatMessageDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chatMessage: ChatMessage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chatMessages: List<ChatMessage>)

    @Query("select * from chatMessage where tag = :tag")
    fun getBy(tag: UUID?): ChatMessage?

    @Query("update chatMessage set status = :status where tag = :tag")
    fun updateStatusBy(tag: UUID?, status: ChatMessageStatus)

    @Query("select * from chatMessage where chatId = :chatId order by id desc, sequence desc limit :loadSize offset :startPosition")
    fun getAllPagedBy(chatId: UUID, startPosition: Int, loadSize: Int): List<ChatMessage>

    @Query("select * from chatMessage where chatId = :chatId and status in (:statuses) order by id desc limit 1")
    fun getLastChatMessageBy(
        chatId: UUID,
        statuses: List<ChatMessageStatus> = listOf(ChatMessageStatus.SENT, ChatMessageStatus.RECEIVED)
    ): ChatMessage?

    @Query("select * from chatMessage where id = :id and chatId = :chatId")
    fun getById(id: Long?, chatId: UUID): ChatMessage?

    @Query("select id from chatMessage where chatId = :chatId and status = :status order by id desc limit 1")
    fun getLastReceivedChatMessageId(chatId: UUID, status: ChatMessageStatus = ChatMessageStatus.RECEIVED): Long?

    @Query("update chatMessage set status = :to where status = :from")
    fun updateStatus(from: ChatMessageStatus, to: ChatMessageStatus)

    @Query("select * from chatMessage where status = :status")
    fun getAllBy(status: ChatMessageStatus): List<ChatMessage>

    @Query("update chatMessage set status = :status, createdAt = :createdAt, id = :id where tag = :tag")
    fun updateAsSentBy(tag: UUID?, id: Long, createdAt: OffsetDateTime?, status: ChatMessageStatus = ChatMessageStatus.SENT)

    @Query("select chatId from chatMessage where tag = :tag")
    fun getChatIdBy(tag: UUID?): UUID?






    @Query("select count(*) > 0 from chatMessage where tag = :tag")
    fun existsBy(tag: UUID?): Boolean






    @Query("select count(*) > 0 from chatMessage where id = :id and chatId = :chatId and status = :status")
    fun existsBy(id: Long, chatId: UUID, status: ChatMessageStatus = ChatMessageStatus.RECEIVED): Boolean











    @Query("select count(*) > 0 from chatMessage where chatId = :chatId and status = :status and `sequence` > :lastReadChatMessageKey limit 1")
    fun existAfter(
        chatId: Long,
        lastReadChatMessageKey: Long,
        status: ChatMessageStatus = ChatMessageStatus.RECEIVED
    ): Boolean





    @Query("update chatMessage set status = :status where id in (:ids)")
    fun updateStatusByIds(ids: List<UUID>, status: ChatMessageStatus)

    @Query("update chatMessage set status = :toStatus where status = :whereStatus and createdAt < :createdAt")
    fun updateStatusBefore(createdAt: OffsetDateTime, whereStatus: ChatMessageStatus, toStatus: ChatMessageStatus)

    @Query("delete from chatMessage where `sequence` = :sequence")
    fun deleteByKey(sequence: Long)

    @Query("delete from chatMessage where chatId = :chatId")
    fun deleteByChatId(chatId: UUID)

    @Query("delete from chatMessage")
    fun deleteAll()

    @Query("select chatId from chatMessage where id = :id")
    fun getChatIdById(id: UUID?): UUID?



    @Query("select count(*) > 0 from chatMessage where id = :id")
    fun existsById(id: UUID?): Boolean



}



//select * from chatmessage order by case when createdAt is null then 1 else createdAt end, `sequence` desc
//insert into chatMessage values (12, 'ddd', 0, null, null, 4)