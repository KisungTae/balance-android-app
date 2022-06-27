package com.beeswork.balance.ui.chatfragment

import androidx.lifecycle.*
import androidx.paging.*
import com.beeswork.balance.data.database.repository.chat.ChatRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.domain.uistate.chat.*
import com.beeswork.balance.domain.uistate.main.WebSocketEventUIState
import com.beeswork.balance.domain.usecase.chat.*
import com.beeswork.balance.domain.usecase.main.ConnectToStompUseCase
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.exception.WebSocketDisconnectedException
import com.beeswork.balance.internal.mapper.chat.ChatMessageMapper
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*


class ChatViewModel(
    private val chatId: UUID,
    private val swipedId: UUID,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val resendChatMessageUseCase: ResendChatMessageUseCase,
    private val getChatMessagePagingDataUseCase: GetChatMessagePagingDataUseCase,
    private val syncMatchUseCase: SyncMatchUseCase,
    private val connectToStompUseCase: ConnectToStompUseCase,
    private val deleteChatMessageUseCase: DeleteChatMessageUseCase,
    private val unmatchUseCase: UnmatchUseCase,
    private val chatRepository: ChatRepository,
    private val matchRepository: MatchRepository,
    private val preferenceProvider: PreferenceProvider,
    private val chatMessageMapper: ChatMessageMapper,
    private val matchMapper: MatchMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    val matchUIStateLiveData by viewModelLazyDeferred {
        matchRepository.getMatchFlow(chatId).map { match ->
            if (match == null) {
                null
            } else {
                matchMapper.toItemUIState(match)
            }
        }.asLiveData(viewModelScope.coroutineContext + defaultDispatcher)
    }

    val chatPageInvalidationUIStateLiveData by viewModelLazyDeferred {
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
        }.asLiveData(viewModelScope.coroutineContext + defaultDispatcher)
    }

    val webSocketEventUIStateLiveData by viewModelLazyDeferred {
        chatRepository.getWebSocketEventFlow().map { webSocketEvent ->
            WebSocketEventUIState(webSocketEvent.status, false, webSocketEvent.exception)
        }.asLiveData(viewModelScope.coroutineContext + defaultDispatcher)
    }

    private val _sendChatMessageUIStateLiveData = MutableLiveData<SendChatMessageUIState>()
    val sendChatMessageUIStateLiveData: LiveData<SendChatMessageUIState> = _sendChatMessageUIStateLiveData

    private val _resendChatMessageUIStateLiveData = MutableLiveData<ResendChatMessageUIState>()
    val resendChatMessageUIStateLiveData: LiveData<ResendChatMessageUIState> = _resendChatMessageUIStateLiveData

    private val _unmatchLiveData = MutableLiveData<UnmatchUIState>()
    val unmatchLiveData: LiveData<UnmatchUIState> get() = _unmatchLiveData

    fun sendChatMessage(body: String) {
        viewModelScope.launch {
            val response = sendChatMessageUseCase.invoke(chatId, body)
            val sendChatMessageUIState = if (response.isSuccess()) {
                SendChatMessageUIState.ofSuccess()
            } else {
                SendChatMessageUIState.ofError(
                    clearChatMessageInput = response.exception is WebSocketDisconnectedException,
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
                ResendChatMessageUIState.ofError(exception = response.exception)
            }
            _resendChatMessageUIStateLiveData.postValue(resendChatMessageUIState)
        }
    }

    fun getChatMessagePagingData(): LiveData<PagingData<ChatMessageItemUIState>> {
        return getChatMessagePagingDataUseCase.invoke(chatId, viewModelScope)
    }

    fun syncMatch() {
        viewModelScope.launch {
            syncMatchUseCase.invoke(chatId)
        }
    }

    fun connectToStomp() {
        viewModelScope.launch {
            connectToStompUseCase.invoke(true)
        }
    }

    fun deleteChatMessage(tag: UUID) {
        viewModelScope.launch {
            deleteChatMessageUseCase.invoke(chatId, tag)
        }
    }
    
    fun unmatch() {
        viewModelScope.launch {
            _unmatchLiveData.postValue(UnmatchUIState.ofLoading())
            val response = unmatchUseCase.onInvoke(chatId, swipedId)
            val unmatchUIState = if (response.isSuccess()) {
                UnmatchUIState.ofSuccess()
            } else {
                UnmatchUIState.ofError(response.exception)
            }
            _unmatchLiveData.postValue(unmatchUIState)
        }
    }

    fun test() {
        chatRepository.test()
    }
}