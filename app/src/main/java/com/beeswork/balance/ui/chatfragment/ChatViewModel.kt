package com.beeswork.balance.ui.chatfragment

import androidx.lifecycle.*
import androidx.paging.*
import com.beeswork.balance.data.database.repository.chat.ChatRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.database.repository.chat.ChatMessageInvalidation
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.DateTimePattern
import com.beeswork.balance.internal.constant.ReportReason
import com.beeswork.balance.internal.exception.ChatMessageEmptyException
import com.beeswork.balance.internal.exception.ChatMessageOverSizedException
import com.beeswork.balance.internal.exception.MatchUnmatchedException
import com.beeswork.balance.internal.mapper.chat.ChatMessageMapper
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*


class ChatViewModel(
    private val chatId: UUID,
    private val swipedId: UUID,
    private val chatRepository: ChatRepository,
    private val matchRepository: MatchRepository,
    private val chatMessageMapper: ChatMessageMapper,
    private val matchMapper: MatchMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    val matchLiveData by viewModelLazyDeferred {
        matchRepository.getMatchFlow().map { match ->
            if (match == null) {
                null
            } else {
                matchMapper.toMatchDomain(match)
            }
        }.asLiveData(viewModelScope.coroutineContext + defaultDispatcher)
    }

//    val chatMessageInvalidationLiveData by viewModelLazyDeferred {
//        chatRepository.chatMessageInvalidationFlow.filter { chatMessageInvalidation ->
//            chatMessageInvalidation.type == ChatMessageInvalidation.Type.FETCHED || chatMessageInvalidation.chatId == chatId
//        }.asLiveData()
//    }

    private val _sendChatMessageLiveData = MutableLiveData<Resource<EmptyResponse>>()
    private val sendChatMessageLiveData: LiveData<Resource<EmptyResponse>> get() = _sendChatMessageLiveData
    val sendChatMessageMediatorLiveData = MediatorLiveData<Resource<EmptyResponse>>()

    private val _reportMatchLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val reportMatchLiveData: LiveData<Resource<EmptyResponse>> get() = _reportMatchLiveData

    private val _unmatchLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val unmatchLiveData: LiveData<Resource<EmptyResponse>> get() = _unmatchLiveData

    init {
        sendChatMessageMediatorLiveData.addSource(sendChatMessageLiveData) {
            sendChatMessageMediatorLiveData.postValue(it)
        }
        sendChatMessageMediatorLiveData.addSource(chatRepository.chatMessageReceiptFlow.asLiveData()) {
            sendChatMessageMediatorLiveData.postValue(it)
        }
    }

    fun initChatMessagePagingData(): LiveData<PagingData<ChatMessageDomain>> {
        return Pager(
            pagingConfig,
            null,
            { ChatMessagePagingSource(chatRepository, chatId) }
        ).flow.cachedIn(viewModelScope).map { pagingData ->
            var prevChatMessageDomain: ChatMessageDomain? = null
            pagingData.map { chatMessage ->
                val chatMessageDomain = chatMessageMapper.toDomain(chatMessage)
                if (prevChatMessageDomain != null) {
                    if (chatMessageDomain.status.isProcessed()
                        && chatMessageDomain.status == prevChatMessageDomain?.status
                        && chatMessageDomain.dateCreatedAt == prevChatMessageDomain?.dateCreatedAt
                        && chatMessageDomain.timeCreatedAt == prevChatMessageDomain?.timeCreatedAt
                    ) {
                        prevChatMessageDomain?.showProfilePhoto = false
                        chatMessageDomain.showTime = false
                    }
                }
                prevChatMessageDomain = chatMessageDomain
                chatMessageDomain
            }.insertSeparators { before: ChatMessageDomain?, after: ChatMessageDomain? ->
                var separator: ChatMessageDomain? = null
                if (before?.status?.isProcessed() == true && (after?.dateCreatedAt == null || before.dateCreatedAt != after.dateCreatedAt)) {
                    separator = ChatMessageDomain.toSeparator(
                        before.dateCreatedAt?.format(DateTimePattern.ofDateWithDayOfWeek())
                    )
                }
                separator
            }
        }.asLiveData(viewModelScope.coroutineContext + defaultDispatcher)
    }

    fun sendChatMessage(body: String) {
        viewModelScope.launch {
            val bodySize = body.toByteArray().size
            when {
                bodySize > MAX_CHAT_MESSAGE_BODY_SIZE -> _sendChatMessageLiveData.postValue(
                    Resource.error(ChatMessageOverSizedException())
                )
                bodySize <= 0 -> _sendChatMessageLiveData.postValue(
                    Resource.error(ChatMessageEmptyException())
                )
//                else -> _sendChatMessageLiveData.postValue(
//                    chatRepository.sendChatMessage(chatId, swipedId, body)
//                )
            }
        }
    }

    fun deleteChatMessage(key: Long) {
//        viewModelScope.launch { chatRepository.deleteChatMessage(chatId, key) }
    }

    fun resendChatMessage(chatMessageId: UUID?) {
        viewModelScope.launch { _sendChatMessageLiveData.postValue(chatRepository.resendChatMessage(chatMessageId)) }
    }

    fun unmatch() {
        viewModelScope.launch {
            _unmatchLiveData.postValue(Resource.loading())
//            val response = matchRepository.unmatch(chatId, swipedId)
//            _unmatchLiveData.postValue(response)
        }
    }

    fun reportMatch(reportReason: ReportReason, description: String) {
        viewModelScope.launch {
            _reportMatchLiveData.postValue(Resource.loading())
//            val response = matchRepository.reportMatch(chatId, swipedId, reportReason, description)
//            _reportMatchLiveData.postValue(response)
        }
    }

    fun test() {
        chatRepository.test()
    }

    fun test2() {
//        _sendChatMessageLiveData.postValue(Resource.error("error"))
    }

    companion object {
        private const val MAX_CHAT_MESSAGE_BODY_SIZE = 500
        private const val CHAT_PAGE_SIZE = 80
        private const val CHAT_PAGE_PREFETCH_DISTANCE = CHAT_PAGE_SIZE
        private const val CHAT_MAX_PAGE_SIZE = CHAT_PAGE_PREFETCH_DISTANCE * 3 + CHAT_PAGE_SIZE
        private val pagingConfig = PagingConfig(
            CHAT_PAGE_SIZE,
            CHAT_PAGE_PREFETCH_DISTANCE,
            false,
            CHAT_PAGE_SIZE,
            CHAT_MAX_PAGE_SIZE
        )
    }
}