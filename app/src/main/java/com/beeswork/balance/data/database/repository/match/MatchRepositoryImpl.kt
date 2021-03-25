package com.beeswork.balance.data.database.repository.match

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import com.beeswork.balance.data.database.BalanceDatabase
import com.beeswork.balance.data.database.dao.*
import com.beeswork.balance.data.database.entity.*
import com.beeswork.balance.data.network.rds.chat.ChatRDS
import com.beeswork.balance.data.network.rds.match.MatchRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.data.network.response.common.EmptyResponse
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

    private val _fetchMatchesLiveData = MutableLiveData<Resource<EmptyResponse>>()
    override val fetchMatchesLiveData: LiveData<Resource<EmptyResponse>> get() = _fetchMatchesLiveData

    override suspend fun loadMatches(loadSize: Int, startPosition: Int): List<Match> {
        return withContext(Dispatchers.IO) {
            return@withContext matchDAO.findAllPaged(loadSize, startPosition) ?: listOf()
        }
    }

    override suspend fun loadMatches(loadSize: Int, startPosition: Int, searchKeyword: String): List<Match> {
        return withContext(Dispatchers.IO) {
            return@withContext matchDAO.findAllPaged(loadSize, startPosition, "%${searchKeyword}%") ?: listOf()
        }
    }

    override suspend fun fetchMatches() {
        val listMatches = matchRDS.listMatches(
            preferenceProvider.getAccountId(),
            preferenceProvider.getIdentityToken(),
            preferenceProvider.getMatchFetchedAt()
        )

        if (listMatches.isError()) {
            _fetchMatchesLiveData.postValue(Resource.toEmptyResponse(listMatches))
            return
        }

        listMatches.data?.let { data ->
            //      TODO: remove me
            saveSentChatMessages(
                data.sentChatMessageDTOs.map { chatMessageMapper.fromDTOToEntity(it) },
                data.matchDTOs.map { matchMapper.fromDTOToEntity(it) }
            )

            saveChatMessages(
                data.sentChatMessageDTOs.map { chatMessageMapper.fromDTOToEntity(it) },
                data.receivedChatMessageDTOs.map { chatMessageMapper.fromDTOToEntity(it) }
            )
            syncChatMessages(data.sentChatMessageDTOs, data.receivedChatMessageDTOs)
            saveMatches(data.matchDTOs.map { matchMapper.fromDTOToEntity(it) })
            preferenceProvider.putMatchFetchedAt(data.fetchedAt)
        }
        _fetchMatchesLiveData.postValue(Resource.toEmptyResponse(listMatches))
    }

    private fun saveChatMessages(
        sentChatMessages: List<ChatMessage>,
        receivedChatMessages: List<ChatMessage>
    ) {
        balanceDatabase.runInTransaction {
            chatMessageDAO.insert(receivedChatMessages)
            sentChatMessages.forEach {
                chatMessageDAO.updateSentMessage(it.messageId, it.id, it.status, it.createdAt)
            }
        }
    }

    private fun syncChatMessages(
        sentChatMessages: List<ChatMessageDTO>,
        receivedChatMessages: List<ChatMessageDTO>
    ) {
        if (sentChatMessages.isEmpty() && receivedChatMessages.isEmpty()) return
        CoroutineScope(Dispatchers.IO).launch(CoroutineExceptionHandler { c, t -> }) {
            chatRDS.syncChatMessages(
                preferenceProvider.getAccountId(),
                preferenceProvider.getIdentityToken(),
                sentChatMessages.map { it.id },
                receivedChatMessages.map { it.id }
            )
        }
    }

    private fun saveMatches(matches: List<Match>) {
        val matchedIds = mutableListOf<UUID>()
        val clickedList = mutableListOf<Clicked>()

        matches.forEach {
            updateMatch(it)
            matchedIds.add(it.matchedId)
            clickedList.add(Clicked(it.matchedId))
        }

        balanceDatabase.runInTransaction {
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
        updateRecentChatMessage(match)
    }

    private fun updateRecentChatMessage(match: Match) {
        chatMessageDAO.findMostRecentAfter(
            match.chatId,
            match.lastReadChatMessageId
        )?.let { chatMessage ->
            match.recentChatMessage = chatMessage.body
            match.updatedAt = chatMessage.createdAt
            match.active = true
            match.unread = true
        }
    }


    //  TODO: remove me
    private fun saveSentChatMessages(sentChatMessages: List<ChatMessage>, matches: List<Match>) {
        val chatIds = matches.map { it.chatId }
        for (msg in sentChatMessages) {
            val randomIndex = Random.nextInt(0, chatIds.size - 1)
            chatMessageDAO.insert(
                ChatMessage(
                    msg.id,
                    chatIds[randomIndex],
                    "message-${Random.nextFloat()}",
                    ChatMessageStatus.SENDING,
                    OffsetDateTime.now(ZoneOffset.UTC),
                    msg.messageId,
                )
            )
        }
    }

    //  TODO: remove me
    override fun testFunction() {

        val dummyMatches = mutableListOf<Match>()


        for (i in 101..103) {
            dummyMatches.add(
                Match(
                    i.toLong(),
                    UUID.randomUUID(),
                    false,
                    false,
                    "user-$i test",
                    "",
                    false,
                    OffsetDateTime.now()
                )
            )
        }

        CoroutineScope(Dispatchers.IO).launch {
            matchDAO.insert(dummyMatches)
            _fetchMatchesLiveData.postValue(Resource.success(EmptyResponse()))
//            matchDAO.insert(dummyMatches[0])


//            matchDAO.findById(3844)?.let {
//                it.updatedAt = OffsetDateTime.now()
//                it.name = "this is updated user"
//                matchDAO.insert(it)
//            }
        }

    }
}
