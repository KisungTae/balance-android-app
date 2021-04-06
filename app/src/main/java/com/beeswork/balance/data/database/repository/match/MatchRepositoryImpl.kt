package com.beeswork.balance.data.database.repository.match

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.beeswork.balance.data.database.BalanceDatabase
import com.beeswork.balance.data.database.dao.*
import com.beeswork.balance.data.database.entity.*
import com.beeswork.balance.data.database.response.NewChatMessage
import com.beeswork.balance.data.database.response.NewMatch
import com.beeswork.balance.data.database.response.PagingRefresh
import com.beeswork.balance.data.network.rds.chat.ChatRDS
import com.beeswork.balance.data.network.rds.match.MatchRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.mapper.chat.ChatMessageMapper
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.*
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import java.util.*
import kotlin.random.Random


class MatchRepositoryImpl(
    private val matchRDS: MatchRDS,
    private val chatRDS: ChatRDS,
    private val chatMessageDAO: ChatMessageDAO,
    private val matchDAO: MatchDAO,
    private val clickerDAO: ClickerDAO,
    private val clickedDAO: ClickedDAO,
    private val matchMapper: MatchMapper,
    private val chatMessageMapper: ChatMessageMapper,
    private val balanceDatabase: BalanceDatabase,
    private val preferenceProvider: PreferenceProvider
) : MatchRepository {

    private val _matchPagingRefreshLiveData = MutableLiveData<PagingRefresh<NewMatch>>()
    override val matchPagingRefreshLiveData: LiveData<PagingRefresh<NewMatch>> get() = _matchPagingRefreshLiveData

    private val _chatMessagePagingRefreshLiveData = MutableLiveData<PagingRefresh<NewChatMessage>>()
    override val chatMessagePagingRefreshLiveData: LiveData<PagingRefresh<NewChatMessage>> get() = _chatMessagePagingRefreshLiveData


    private val _fetchMatchesLiveData = MutableLiveData<Resource<EmptyResponse>>()
    override val fetchMatchesLiveData: LiveData<Resource<EmptyResponse>> get() = _fetchMatchesLiveData

    override suspend fun loadMatches(loadSize: Int, startPosition: Int): List<Match> {
        return withContext(Dispatchers.IO) {
            return@withContext matchDAO.findAllPaged(loadSize, startPosition)
        }
    }

    override suspend fun loadMatches(loadSize: Int, startPosition: Int, searchKeyword: String): List<Match> {
        return withContext(Dispatchers.IO) {
            return@withContext matchDAO.findAllPaged(loadSize, startPosition, "%${searchKeyword}%")
        }
    }

    override suspend fun fetchMatches(): Resource<EmptyResponse> {
        return withContext(Dispatchers.IO) {
            val listMatches = matchRDS.listMatches(
                preferenceProvider.getAccountId(),
                preferenceProvider.getIdentityToken(),
                preferenceProvider.getMatchFetchedAt()
            )

            if (listMatches.isError())
//                _fetchMatchesLiveData.postValue(Resource.toEmptyResponse(listMatches))
            else {
                listMatches.data?.let { data ->
                    saveMatches(data.matchDTOs)
                    saveChatMessages(data.sentChatMessageDTOs, data.receivedChatMessageDTOs)
                    preferenceProvider.putMatchFetchedAt(data.fetchedAt)
                    _chatMessagePagingRefreshLiveData.postValue(PagingRefresh(null))
                    _matchPagingRefreshLiveData.postValue(PagingRefresh(null))
                }
//                _fetchMatchesLiveData.postValue(Resource.toEmptyResponse(listMatches))
            }

            return@withContext Resource.error("")
        }
    }

    private fun saveChatMessages(
        sentChatMessageDTOs: List<ChatMessageDTO>,
        receivedChatMessageDTOs: List<ChatMessageDTO>
    ) {
        val receivedChatMessages = receivedChatMessageDTOs.map { chatMessageMapper.fromDTOToEntity(it) }
        val chatIds = mutableSetOf<Long>()
        val sentChatMessageIds = mutableListOf<Long>()
        val receivedChatMessageIds = mutableListOf<Long>()

        balanceDatabase.runInTransaction {
            receivedChatMessages.forEach {
                if (!chatMessageDAO.existsById(it.id))
                    chatMessageDAO.insert(it)
                chatIds.add(it.chatId)
                receivedChatMessageIds.add(it.id)
            }

            sentChatMessageDTOs.forEach { chatMessageDTO ->
                chatMessageDTO.key?.let { key ->
                    chatMessageDAO.findByKey(key)?.let { chatMessage ->
                        chatMessage.id = chatMessageDTO.id
                        chatMessage.status = ChatMessageStatus.SENT
                        chatMessage.createdAt = chatMessageDTO.createdAt
                        chatMessageDAO.insert(chatMessage)
                        chatIds.add(chatMessage.chatId)
                    }
                }
                sentChatMessageIds.add(chatMessageDTO.id)
            }
        }
        syncChatMessages(sentChatMessageIds, receivedChatMessageIds)
        updateRecentChatMessages(chatIds)
    }

    private fun updateRecentChatMessages(chatIds: Set<Long>) {
        balanceDatabase.runInTransaction {
            chatIds.forEach { chatId ->
                matchDAO.findById(chatId)?.let { match ->
                    updateRecentChatMessage(match)
                    matchDAO.insert(match)
                }
            }
        }
    }

    private fun updateRecentChatMessage(match: Match) {
        chatMessageDAO.findMostRecentAfter(
            match.chatId,
            match.lastReadChatMessageId
        )?.let { chatMessage ->
            match.recentChatMessage = chatMessage.body
            chatMessage.createdAt?.let { match.updatedAt = it }
            match.active = true
        }
        match.unread = chatMessageDAO.unreadExists(match.chatId, match.lastReadChatMessageId)
    }


    private fun syncChatMessages(
        sentChatMessageIds: List<Long>,
        receivedChatMessageIds: List<Long>
    ) {
        if (sentChatMessageIds.isEmpty() && receivedChatMessageIds.isEmpty()) return
        CoroutineScope(Dispatchers.IO).launch(CoroutineExceptionHandler { c, t -> }) {
            chatRDS.syncChatMessages(
                preferenceProvider.getAccountId(),
                preferenceProvider.getIdentityToken(),
                sentChatMessageIds,
                receivedChatMessageIds
            )
        }
    }

    private fun saveMatches(matchDTOs: List<MatchDTO>) {
        val matches = matchDTOs.map { matchMapper.fromDTOToEntity(it) }
        val matchedIds = mutableListOf<UUID>()
        val clickedList = mutableListOf<Clicked>()

        balanceDatabase.runInTransaction {
            matches.forEach {
                updateMatch(it)
                matchedIds.add(it.matchedId)
                clickedList.add(Clicked(it.matchedId))
            }

            matchDAO.insert(matches)
            clickerDAO.deleteInIds(matchedIds)
            clickedDAO.insert(clickedList)
        }
    }

    private fun updateMatch(match: Match) {
        matchDAO.findById(match.chatId)?.let {
            match.updatedAt = it.updatedAt
            match.unread = it.unread
            match.active = it.active
            match.recentChatMessage = it.recentChatMessage
            match.lastReadChatMessageId = it.lastReadChatMessageId
        }
    }

    override suspend fun sendChatMessage(chatId: Long, body: String) {
        chatMessageDAO.insert(ChatMessage(chatId, body, ChatMessageStatus.SENDING, null))
    }

    override suspend fun loadChatMessages(loadSize: Int, startPosition: Int, chatId: Long): List<ChatMessage> {
        return withContext(Dispatchers.IO) {
            return@withContext chatMessageDAO.findAllPaged(loadSize, startPosition, chatId)
        }
    }

    override suspend fun updateRecentChatMessage(chatId: Long, chatMessageId: Long) {
        chatMessageDAO.findBodyById(chatMessageId)?.let { body ->

        }
    }


    override fun createDummyChatMessage() {
        val messages = mutableListOf<ChatMessage>()
        var count = 1L

        var now = OffsetDateTime.now()
        val chatId = 354L

        for (i in 1..10) {
            val status = if (Random.nextBoolean()) ChatMessageStatus.SENDING else ChatMessageStatus.ERROR
            messages.add(ChatMessage(chatId, "$count - ${Random.nextLong()}", status, null))
            count++
        }

        for (i in 0..100) {
            var createdAt = now.plusMinutes(Random.nextInt(10).toLong())
            for (j in 0..Random.nextInt(10)) {
                if ((Random.nextInt(3) + 1) % 3 == 0) createdAt = createdAt.plusMinutes(Random.nextInt(10).toLong())
                val status = if (Random.nextBoolean()) ChatMessageStatus.SENT else ChatMessageStatus.RECEIVED
                messages.add(ChatMessage(chatId, "$count - ${Random.nextLong()}", status, createdAt, count))
                count++
            }
            now = now.plusDays(1)
        }
        chatMessageDAO.insert(messages)
    }


    //  TODO: remove me
    private fun saveSentChatMessages(sentChatMessages: List<ChatMessage>, matches: List<Match>) {
        val chatIds = matches.map { it.chatId }
        for (msg in sentChatMessages) {
            val randomIndex = Random.nextInt(0, chatIds.size - 1)
            chatMessageDAO.insert(
                ChatMessage(
                    chatIds[randomIndex],
                    "message-${Random.nextFloat()}",
                    ChatMessageStatus.SENDING,
                    OffsetDateTime.now(ZoneOffset.UTC),
                    msg.key,
                    msg.id,
                )
            )
        }
    }

    //  TODO: remove me
    override fun testFunction() {
        val chatMessages = mutableListOf<ChatMessage>()
        chatMessages.add(
            ChatMessage(
                353,
                "message-0.55419207",
                ChatMessageStatus.SENDING,
                null,
                Long.MAX_VALUE,
                3
            )
        )

        chatMessages.add(
            ChatMessage(
                353,
                "message-0.9818386",
                ChatMessageStatus.SENDING,
                null,
                Long.MAX_VALUE,
                4
            )
        )

        CoroutineScope(Dispatchers.IO).launch {
            chatMessageDAO.insert(chatMessages)
        }


//        val dummyMatches = mutableListOf<Match>()
//
//
//        for (i in 101..103) {
//            dummyMatches.add(
//                Match(
//                    i.toLong(),
//                    UUID.randomUUID(),
//                    false,
//                    false,
//                    "user-$i test",
//                    "",
//                    false,
//                    OffsetDateTime.now()
//                )
//            )
//        }
//
//        CoroutineScope(Dispatchers.IO).launch {
//            matchDAO.insert(dummyMatches)
//            _fetchMatchesLiveData.postValue(Resource.success(EmptyResponse()))
////            matchDAO.insert(dummyMatches[0])
//
//
////            matchDAO.findById(3844)?.let {
////                it.updatedAt = OffsetDateTime.now()
////                it.name = "this is updated user"
////                matchDAO.insert(it)
////            }
//        }

    }
}
