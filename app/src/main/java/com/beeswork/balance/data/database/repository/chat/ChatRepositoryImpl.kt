package com.beeswork.balance.data.database.repository.chat

import com.beeswork.balance.data.database.dao.ChatMessageDAO
import com.beeswork.balance.data.database.dao.MatchDAO
import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.network.rds.chat.ChatRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.ui.chat.ChatMessageDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime
import kotlin.random.Random

class ChatRepositoryImpl(
    private val chatRDS: ChatRDS,
    private val chatMessageDAO: ChatMessageDAO,
    private val matchDAO: MatchDAO,
    private val preferenceProvider: PreferenceProvider
) : ChatRepository {

    override suspend fun sendChatMessage(chatId: Long, body: String) {
        chatMessageDAO.insert(ChatMessage(chatId, body, ChatMessageStatus.SENDING, null))
    }

    override suspend fun loadChatMessages(loadSize: Int, startPosition: Int, chatId: Long): List<ChatMessage> {
        return withContext(Dispatchers.IO) {
            return@withContext chatMessageDAO.findAllPaged(loadSize, startPosition, chatId)
        }
    }






    override suspend fun test() {
        val messages = mutableListOf<ChatMessage>()
        var count = 1L

        var now = OffsetDateTime.now()

        for (i in 1..10) {
            val status = if (Random.nextBoolean()) ChatMessageStatus.SENDING else ChatMessageStatus.ERROR
            messages.add(ChatMessage(1L, "$count - ${Random.nextLong()}" , status, OffsetDateTime.now()))
            count++
        }

        for (i in 0..9) {
            var createdAt = now.minusSeconds(Random.nextInt(10).toLong())
            for (j in 0..Random.nextInt(50)) {
                if ((Random.nextInt(4) + 1) % 4 == 0) createdAt = now.minusMinutes(Random.nextInt(10).toLong())
                val status = if (Random.nextBoolean()) ChatMessageStatus.SENT else ChatMessageStatus.RECEIVED
                messages.add(ChatMessage(1L, "$count - ${Random.nextLong()}", status, createdAt, count))
                count++
            }
            now = now.minusDays(1)
        }


//        for (i in 1..200) {
//            count++
//            val status = if (Random.nextBoolean()) ChatMessageStatus.SENT else ChatMessageStatus.RECEIVED
//            messages.add(ChatMessage(count, 276L, Random.nextLong().toString(), status, OffsetDateTime.now()))
//        }
//
//        for (i in 1..10) {
//            count++
//            val status = if (Random.nextBoolean()) ChatMessageStatus.SENDING else ChatMessageStatus.ERROR
//            messages.add(ChatMessage(count, 276L, Random.nextLong().toString(), status, OffsetDateTime.now()))
//        }
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
// insert into `match` values (1, '9319abfc-91d2-4296-8ead-7a7ec516fecf ', 0, 0, 'Lisa', 'rep photo key', 0, '2020-12-20T11:00:00.000Z', 1, 'recen message', 0)