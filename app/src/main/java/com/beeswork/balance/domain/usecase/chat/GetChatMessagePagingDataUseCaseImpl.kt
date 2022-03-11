package com.beeswork.balance.domain.usecase.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.paging.*
import com.beeswork.balance.data.database.repository.chat.ChatRepository
import com.beeswork.balance.domain.uistate.chat.ChatMessageItemUIState
import com.beeswork.balance.internal.constant.DateTimePattern
import com.beeswork.balance.internal.mapper.chat.ChatMessageMapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import java.util.*

class GetChatMessagePagingDataUseCaseImpl(
    private val chatRepository: ChatRepository,
    private val chatMessageMapper: ChatMessageMapper,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : GetChatMessagePagingDataUseCase {

    @ExperimentalPagingApi
    override fun invoke(chatId: UUID, scope: CoroutineScope): LiveData<PagingData<ChatMessageItemUIState>> {
        return Pager(
            config = pagingConfig,
            remoteMediator = ChatMessageRemoteMediator(chatRepository, chatId)
        ) {
            ChatMessagePagingSource(chatRepository, chatId)
        }.flow.cachedIn(scope).map { pagingData ->
            var prevChatMessageItemUIState: ChatMessageItemUIState? = null
            pagingData.map { chatMessage ->
                val chatMessageItemUIState = chatMessageMapper.toItemUIState(chatMessage)
                if (prevChatMessageItemUIState != null) {
                    if (chatMessageItemUIState.status.isProcessed()
                        && chatMessageItemUIState.status == prevChatMessageItemUIState?.status
                        && chatMessageItemUIState.dateCreatedAt == prevChatMessageItemUIState?.dateCreatedAt
                        && chatMessageItemUIState.timeCreatedAt == prevChatMessageItemUIState?.timeCreatedAt
                    ) {
                        prevChatMessageItemUIState?.showProfilePhoto = false
                        chatMessageItemUIState.showTime = false
                    }
                }
                prevChatMessageItemUIState = chatMessageItemUIState
                chatMessageItemUIState
            }.insertSeparators { before: ChatMessageItemUIState?, after: ChatMessageItemUIState? ->
                var separator: ChatMessageItemUIState? = null
                if (before?.status?.isProcessed() == true && (after?.dateCreatedAt == null || before.dateCreatedAt != after.dateCreatedAt)) {
                    val dateCreatedAt = before.dateCreatedAt?.format(DateTimePattern.ofDateWithDayOfWeek())
                    if (dateCreatedAt != null) {
                        separator = ChatMessageItemUIState.ofSeparator(dateCreatedAt)
                    }
                }
                separator
            }
        }.asLiveData(scope.coroutineContext + defaultDispatcher)

    }

    companion object {
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