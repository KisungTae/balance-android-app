package com.beeswork.balance.ui.chat

import androidx.lifecycle.*
import androidx.paging.*
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.DateTimePattern
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.mapper.chat.ChatMessageMapper
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.service.stomp.StompClient
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*


class ChatViewModel(
    private val chatId: Long,
    private val matchedId: UUID,
    private val matchRepository: MatchRepository,
    private val chatMessageMapper: ChatMessageMapper,
    private val matchMapper: MatchMapper,
    private val stompClient: StompClient
) : ViewModel() {

    val chatMessagePagingRefreshLiveData = matchRepository.chatMessagePagingRefreshFlow.asLiveData()

    private val _sendChatMessageLiveData = MutableLiveData<Resource<EmptyResponse>>()
    private val sendChatMessageLiveData: LiveData<Resource<EmptyResponse>> get() = _sendChatMessageLiveData

    val sendChatMessageMediatorLiveData = MediatorLiveData<Resource<EmptyResponse>>()

    init {
        sendChatMessageMediatorLiveData.addSource(sendChatMessageLiveData) {
            sendChatMessageMediatorLiveData.postValue(it)
        }
        sendChatMessageMediatorLiveData.addSource(matchRepository.sendChatMessageFlow.asLiveData()) {
            sendChatMessageMediatorLiveData.postValue(it)
        }
    }

    fun initChatMessagePagingData(): LiveData<PagingData<ChatMessageDomain>> {
        return Pager(
            pagingConfig,
            null,
            { ChatMessagePagingSource(matchRepository, chatId) }
        ).flow.cachedIn(viewModelScope).map { pagingData ->
            pagingData.map { chatMessage -> chatMessageMapper.fromEntityToDomain(chatMessage) }
        }.map { pagingData ->
            var nullifyBeforeTimeCreatedAt = false
            pagingData.insertSeparators { before: ChatMessageDomain?, after: ChatMessageDomain? ->
                val beforeTimeCreatedAt = before?.timeCreatedAt
                if (nullifyBeforeTimeCreatedAt) {
                    before?.timeCreatedAt = null
                    nullifyBeforeTimeCreatedAt = false
                }

                if (after?.isSentOrReceived() == true
                    && after.status == before?.status
                    && after.dateCreatedAt == before.dateCreatedAt
                    && after.timeCreatedAt  == beforeTimeCreatedAt) {
                    before.showRepPhoto = false
                    nullifyBeforeTimeCreatedAt = true
                }

                var separator: ChatMessageDomain? = null
                if (before?.isSentOrReceived() == true
                    && (after?.dateCreatedAt == null || before.dateCreatedAt != after.dateCreatedAt)) {
                    separator = ChatMessageDomain.toSeparator(
                        before.dateCreatedAt?.format(DateTimePattern.ofDateWithDayOfWeek())
                    )
                }
                before?.dateCreatedAt = null
                separator
            }
        }.asLiveData(viewModelScope.coroutineContext)
    }

    fun synchronizeMatch() {
        viewModelScope.launch {
            matchRepository.synchronizeMatch(chatId)
        }
    }

    fun sendChatMessage(body: String) {
        viewModelScope.launch {
            val bodySize = body.toByteArray().size
            when {
                matchRepository.isUnmatched(chatId) -> _sendChatMessageLiveData.postValue(
                    Resource.error(ExceptionCode.MATCH_UNMATCHED_EXCEPTION)
                )
                bodySize > MAX_CHAT_MESSAGE_BODY_SIZE -> _sendChatMessageLiveData.postValue(
                    Resource.error(ExceptionCode.CHAT_MESSAGE_OVER_SIZED_EXCEPTION)
                )
                bodySize <= 0 -> _sendChatMessageLiveData.postValue(
                    Resource.error(ExceptionCode.CHAT_MESSAGE_EMPTY_EXCEPTION)
                )
                else -> matchRepository.sendChatMessage(chatId, matchedId, body)
            }
        }
    }

    fun deleteChatMessage(key: Long) {
        viewModelScope.launch { matchRepository.deleteChatMessage(key) }
    }

    fun resendChatMessage(key: Long) {
        viewModelScope.launch { matchRepository.resendChatMessage(key, matchedId) }
    }

    fun test() {
        matchRepository.testFunction()
    }

    fun test2() {
        _sendChatMessageLiveData.postValue(Resource.error("error"))
    }

    companion object {
        private const val MAX_CHAT_MESSAGE_BODY_SIZE = 500
        private const val CHAT_PAGE_SIZE = 80
        private const val CHAT_PREFETCH_DISTANCE = CHAT_PAGE_SIZE
        private const val CHAT_MAX_PAGE_SIZE = CHAT_PREFETCH_DISTANCE * 3 + CHAT_PAGE_SIZE
        private val pagingConfig = PagingConfig(
            CHAT_PAGE_SIZE,
            CHAT_PREFETCH_DISTANCE,
            false,
            CHAT_PAGE_SIZE,
            CHAT_MAX_PAGE_SIZE
        )
    }
}


// TODO: websocket receives dto, pass it from viewmodel to repository, map to entity and save
// TODO: fetchMatches observer check validate of parent