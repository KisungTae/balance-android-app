package com.beeswork.balance.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.data.database.repository.chat.ChatRepository
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.mapper.chat.ChatMessageMapper
import com.beeswork.balance.internal.util.safeLet
import com.beeswork.balance.service.stomp.StompClient
import com.beeswork.balance.ui.match.MatchDomain
import com.beeswork.balance.ui.match.MatchPagingSource
import com.beeswork.balance.ui.match.MatchViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime


class ChatViewModel(
    private val chatId: Long,
    private val chatRepository: ChatRepository,
    private val chatMessageMapper: ChatMessageMapper,
    private val stompClient: StompClient
) : ViewModel() {

    private val pagingConfig = PagingConfig(
        CHAT_PAGE_SIZE,
        CHAT_PREFETCH_DISTANCE,
        false,
        CHAT_PAGE_SIZE,
        CHAT_MAX_PAGE_SIZE
    )

    fun initializeChatPagingData(searchKeyword: String): Flow<PagingData<ChatMessageDomain>> {
        return Pager(
            pagingConfig,
            null,
            { ChatMessagePagingSource(chatRepository, "", null) }
        ).flow.cachedIn(viewModelScope).map { pagingData ->
            pagingData.map { chatMessageMapper.fromEntityToDomain(it) }
        }.map {
            it.insertSeparators { before: ChatMessageDomain?, after: ChatMessageDomain? ->
//                println("${before?.key} - ${after?.key}")
//
//                safeLet(before, after) { b, a ->
//                    if (b.createdAt?.)
//                }
                null
            }
        }
    }


    fun test() {
        CoroutineScope(Dispatchers.IO).launch { chatRepository.test() }
    }

    companion object {
        private const val CHAT_PAGE_SIZE = 80
        private const val CHAT_PREFETCH_DISTANCE = CHAT_PAGE_SIZE
        private const val CHAT_MAX_PAGE_SIZE = CHAT_PREFETCH_DISTANCE * 3 + CHAT_PAGE_SIZE
    }
}


