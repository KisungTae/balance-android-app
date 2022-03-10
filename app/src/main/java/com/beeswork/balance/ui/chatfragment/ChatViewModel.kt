package com.beeswork.balance.ui.chatfragment

import androidx.lifecycle.*
import androidx.paging.*
import com.beeswork.balance.data.database.repository.chat.ChatRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.domain.uistate.chat.ChatPageInvalidationUIState
import com.beeswork.balance.domain.uistate.chat.ChatMessageItemUIState
import com.beeswork.balance.domain.uistate.chat.ResendChatMessageUIState
import com.beeswork.balance.domain.usecase.chat.SendChatMessageUseCase
import com.beeswork.balance.domain.uistate.chat.SendChatMessageUIState
import com.beeswork.balance.domain.usecase.chat.GetChatMessagePagingDataUseCase
import com.beeswork.balance.domain.usecase.chat.ResendChatMessageUseCase
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.constant.ReportReason
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
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val resendChatMessageUseCase: ResendChatMessageUseCase,
    private val getChatMessagePagingDataUseCase: GetChatMessagePagingDataUseCase,
    private val chatRepository: ChatRepository,
    private val matchRepository: MatchRepository,
    private val chatMessageMapper: ChatMessageMapper,
    private val matchMapper: MatchMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    val matchLiveData by viewModelLazyDeferred {
        matchRepository.getMatchFlow(chatId).map { match ->
            if (match == null) {
                null
            } else {
                matchMapper.toItemUIState(match)
            }
        }.asLiveData(viewModelScope.coroutineContext + defaultDispatcher)
    }

    val chatPageInvalidationLiveData by viewModelLazyDeferred {
        chatRepository.chatPageInvalidationFlow.filter { chatMessage ->
            if (chatMessage == null) {
                true
            } else {
                chatMessage.chatId == chatId
            }
        }.map { chatMessage ->
            when (chatMessage?.status) {
                ChatMessageStatus.RECEIVED -> {
                    ChatPageInvalidationUIState(false, chatMessage.body)
                }
                ChatMessageStatus.SENDING -> {
                    ChatPageInvalidationUIState(true, null)
                }
                else -> {
                    null
                }
            }
        }.asLiveData(viewModelScope.coroutineContext)
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


    private val _sendChatMessageUIStateLiveData = MutableLiveData<SendChatMessageUIState>()
    val sendChatMessageUIStateLiveData: LiveData<SendChatMessageUIState> = _sendChatMessageUIStateLiveData

    private val _resendChatMessageUIStateLiveData = MutableLiveData<ResendChatMessageUIState>()
    val resendChatMessageUIStateLiveData: LiveData<ResendChatMessageUIState> = _resendChatMessageUIStateLiveData


    fun sendChatMessage(body: String) {
        viewModelScope.launch {
            val response = sendChatMessageUseCase.invoke(chatId, body)
            val sendChatMessageUIState = if (response.isSuccess()) {
                SendChatMessageUIState.ofSuccess()
            } else {
                SendChatMessageUIState.ofError(
                    shouldLogout = ExceptionCode.isLoginException(response.exception),
                    exception = response.exception
                )
            }
            _sendChatMessageUIStateLiveData.postValue(sendChatMessageUIState)
        }
    }

    fun resendChatMessage(tag: UUID) {
        viewModelScope.launch {
            val response = resendChatMessageUseCase.invoke(chatId, tag)
            val resendChatMessageUIState = if (response.isSuccess()) {
                ResendChatMessageUIState.ofSuccess()
            } else {
                ResendChatMessageUIState.ofError(
                    shouldLogout = ExceptionCode.isLoginException(response.exception),
                    exception = response.exception
                )
            }
            _resendChatMessageUIStateLiveData.postValue(resendChatMessageUIState)
        }
    }

    fun getChatMessagePagingData(): LiveData<PagingData<ChatMessageItemUIState>> {
        return getChatMessagePagingDataUseCase.invoke(chatId, viewModelScope)
    }


    fun deleteChatMessage(key: Long) {
//        viewModelScope.launch { chatRepository.deleteChatMessage(chatId, key) }
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
}