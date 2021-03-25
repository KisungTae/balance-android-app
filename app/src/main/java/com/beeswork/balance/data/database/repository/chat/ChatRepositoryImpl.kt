package com.beeswork.balance.data.database.repository.chat

import com.beeswork.balance.data.database.dao.ChatMessageDAO
import com.beeswork.balance.data.database.dao.MatchDAO
import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.network.rds.chat.ChatRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.ui.chat.ChatMessageDomain
import org.threeten.bp.OffsetDateTime
import kotlin.random.Random

class ChatRepositoryImpl(
    private val chatRDS: ChatRDS,
    private val chatMessageDAO: ChatMessageDAO,
    private val matchDAO: MatchDAO,
    private val preferenceProvider: PreferenceProvider
): ChatRepository {

    override suspend fun loadChatMessages(loadSize: Int, startKey: Long): List<ChatMessage> {

    }


    override suspend fun test() {
        val messages = mutableListOf<ChatMessage>()
        var count = 0L
        for (i in 1..40) {
            count++
            val status = if (Random.nextBoolean()) ChatMessageStatus.SENT else ChatMessageStatus.RECEIVED
            messages.add(ChatMessage(count, 280L, Random.nextLong().toString(), status, OffsetDateTime.now()))
        }

        for (i in 1..10) {
            count++
            val status = if (Random.nextBoolean()) ChatMessageStatus.SENDING else ChatMessageStatus.ERROR
            messages.add(ChatMessage(count, 280L, Random.nextLong().toString(), status, OffsetDateTime.now()))
        }
        chatMessageDAO.insert(messages)
    }

}


// insert into chatMessage values (null, 1, "d", 1, "d", 1)
// UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = 'chatMessage'
// select * from chatMessage
// delete from chatMessage
// select * from chatMessage where id < 21 or id is null order by id desc, messageId desc
// select * from chatMessage order by case when id is null then 0 else 1 end, id desc
// select id, messageId from chatMessage order by case when id is null then 0 else 1 end, id desc
