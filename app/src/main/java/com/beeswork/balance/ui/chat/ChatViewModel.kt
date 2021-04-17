package com.beeswork.balance.ui.chat

import androidx.lifecycle.*
import androidx.paging.*
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.database.response.PagingRefresh
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.DateTimePattern
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.mapper.chat.ChatMessageMapper
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.service.stomp.StompClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
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

    val chatMessagePagingRefreshLiveData = matchRepository.chatMessagePagingRefreshLiveData

    private val _sendChatMessageLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val sendChatMessageLiveData: LiveData<Resource<EmptyResponse>> get() = _sendChatMessageLiveData

    fun initChatMessagePagingData(): LiveData<PagingData<ChatMessageDomain>> {
        return Pager(
            pagingConfig,
            null,
            { ChatMessagePagingSource(matchRepository, chatId) }
        ).flow.cachedIn(viewModelScope).map { pagingData ->
            pagingData.map { chatMessageMapper.fromEntityToDomain(it) }
        }.map { pagingData ->
            pagingData.insertSeparators { before: ChatMessageDomain?, after: ChatMessageDomain? ->
                var separator: ChatMessageDomain? = null
                before?.dateCreatedAt?.let { b ->
                    after?.dateCreatedAt?.let { a ->
                        if (b != a) separator = ChatMessageDomain.toSeparator(
                            b.format(DateTimePattern.ofDateWithDayOfWeek())
                        )
                    } ?: kotlin.run {
                        separator = ChatMessageDomain.toSeparator(
                            b.format(DateTimePattern.ofDateWithDayOfWeek())
                        )
                    }
                }
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
                bodySize > MAX_CHAT_MESSAGE_BODY_SIZE -> _sendChatMessageLiveData.postValue(
                    Resource.error(ExceptionCode.CHAT_MESSAGE_OVER_SIZED_EXCEPTION)
                )
                bodySize <= 0 -> _sendChatMessageLiveData.postValue(
                    Resource.error(ExceptionCode.CHAT_MESSAGE_EMPTY_EXCEPTION)
                )
                else -> {
                    val key = matchRepository.sendChatMessage(chatId, body)
                    stompClient.sendChatMessage(key, chatId, matchedId, body)
                }
            }
        }
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