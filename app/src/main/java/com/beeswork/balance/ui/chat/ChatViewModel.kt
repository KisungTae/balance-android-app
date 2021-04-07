package com.beeswork.balance.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.internal.constant.DateTimePattern
import com.beeswork.balance.internal.mapper.chat.ChatMessageMapper
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.service.stomp.StompClient
import com.beeswork.balance.ui.match.MatchProfileDomain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.temporal.ChronoUnit
import java.util.*


class ChatViewModel(
    private val chatId: Long,
    private val matchedId: UUID,
    private val matchRepository: MatchRepository,
    private val chatMessageMapper: ChatMessageMapper,
    private val matchMapper: MatchMapper,
    private val stompClient: StompClient
) : ViewModel() {


    fun initChatMessagePagingData(): Flow<PagingData<ChatMessageDomain>> {
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
        }
    }


    fun updateRecentChatMessage(chatMessageId: Long) {
        CoroutineScope(Dispatchers.IO).launch {

        }
    }

    fun sendChatMessage(body: String) {
        CoroutineScope(Dispatchers.IO).launch {
//            chatRepository.sendChatMessage(chatId, body)
        }
    }


    fun test() {
        CoroutineScope(Dispatchers.IO).launch { matchRepository.createDummyChatMessage() }
    }

    companion object {
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