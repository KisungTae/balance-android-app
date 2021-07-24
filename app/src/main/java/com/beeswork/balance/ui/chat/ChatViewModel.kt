package com.beeswork.balance.ui.chat

import androidx.lifecycle.*
import androidx.paging.*
import com.beeswork.balance.data.database.repository.chat.ChatRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.database.repository.chat.ChatMessageInvalidation
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.DateTimePattern
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.constant.ReportReason
import com.beeswork.balance.internal.mapper.chat.ChatMessageMapper
import com.beeswork.balance.internal.util.lazyDeferred
import com.beeswork.balance.internal.util.safeLaunch
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*


class ChatViewModel(
    private val chatId: Long,
    private val swipedId: UUID,
    private val chatRepository: ChatRepository,
    private val matchRepository: MatchRepository,
    private val chatMessageMapper: ChatMessageMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    val chatMessageInvalidationLiveData by lazyDeferred {
        chatRepository.chatMessageInvalidationFlow.filter {
            it.type == ChatMessageInvalidation.Type.FETCHED || it.chatId == chatId
        }.asLiveData()
    }

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
            pagingData.map { chatMessage -> chatMessageMapper.toDomain(chatMessage) }
        }.map { pagingData ->
            var nullifyBeforeTimeCreatedAt = false
            pagingData.insertSeparators { before: ChatMessageDomain?, after: ChatMessageDomain? ->
                val beforeTimeCreatedAt = before?.timeCreatedAt
                if (nullifyBeforeTimeCreatedAt) {
                    before?.timeCreatedAt = null
                    nullifyBeforeTimeCreatedAt = false
                }

                if (after?.isProcessed == true
                    && after.status == before?.status
                    && after.dateCreatedAt == before.dateCreatedAt
                    && after.timeCreatedAt == beforeTimeCreatedAt
                ) {
                    before.showProfilePhoto = false
                    nullifyBeforeTimeCreatedAt = true
                }

                var separator: ChatMessageDomain? = null
                if (before?.isProcessed == true
                    && (after?.dateCreatedAt == null || before.dateCreatedAt != after.dateCreatedAt)
                ) {
                    separator = ChatMessageDomain.toSeparator(
                        before.dateCreatedAt?.format(DateTimePattern.ofDateWithDayOfWeek())
                    )
                }
                before?.dateCreatedAt = null
                separator
            }
        }.asLiveData(viewModelScope.coroutineContext + defaultDispatcher)
    }

    fun synchronizeMatch() {
        viewModelScope.launch(coroutineExceptionHandler) { matchRepository.synchronizeMatch(chatId) }
    }

    fun sendChatMessage(body: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
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
                else -> chatRepository.sendChatMessage(chatId, swipedId, body)
            }
        }
    }

    fun deleteChatMessage(key: Long) {
        viewModelScope.launch(coroutineExceptionHandler) { chatRepository.deleteChatMessage(chatId, key) }
    }

    fun resendChatMessage(key: Long) {
        viewModelScope.launch(coroutineExceptionHandler) { chatRepository.resendChatMessage(key, swipedId) }
    }

    fun unmatch() {
        viewModelScope.launch(coroutineExceptionHandler) {
            _unmatchLiveData.postValue(Resource.loading())
            val response = matchRepository.unmatch(chatId, swipedId)
            _unmatchLiveData.postValue(response)
        }
    }

    fun reportMatch(reportReason: ReportReason, description: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _reportMatchLiveData.postValue(Resource.loading())
            val response = matchRepository.reportMatch(chatId, swipedId, reportReason, description)
            _reportMatchLiveData.postValue(response)
        }
    }

    fun test() {
        chatRepository.test()
    }

    fun test2() {
        _sendChatMessageLiveData.postValue(Resource.error("error"))
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