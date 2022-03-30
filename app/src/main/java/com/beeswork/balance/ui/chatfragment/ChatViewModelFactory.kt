package com.beeswork.balance.ui.chatfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.chat.ChatRepository
import com.beeswork.balance.data.database.repository.main.MainRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.domain.usecase.chat.*
import com.beeswork.balance.domain.usecase.main.ConnectToStompUseCase
import com.beeswork.balance.internal.mapper.chat.ChatMessageMapper
import com.beeswork.balance.internal.mapper.match.MatchMapper
import kotlinx.coroutines.CoroutineDispatcher
import java.util.*

class ChatViewModelFactory(
    private val chatViewModelParameter: ChatViewModelParameter,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val resendChatMessageUseCase: ResendChatMessageUseCase,
    private val getChatMessagePagingDataUseCase: GetChatMessagePagingDataUseCase,
    private val syncMatchUseCase: SyncMatchUseCase,
    private val connectToStompUseCase: ConnectToStompUseCase,
    private val deleteChatMessageUseCase: DeleteChatMessageUseCase,
    private val unmatchUseCase: UnmatchUseCase,
    private val reportMatchUseCase: ReportMatchUseCase,
    private val chatRepository: ChatRepository,
    private val matchRepository: MatchRepository,
    private val chatMessageMapper: ChatMessageMapper,
    private val matchMapper: MatchMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatViewModel(
            chatViewModelParameter.chatId,
            chatViewModelParameter.swipedId,
            sendChatMessageUseCase,
            resendChatMessageUseCase,
            getChatMessagePagingDataUseCase,
            syncMatchUseCase,
            connectToStompUseCase,
            deleteChatMessageUseCase,
            unmatchUseCase,
            reportMatchUseCase,
            chatRepository,
            matchRepository,
            chatMessageMapper,
            matchMapper,
            defaultDispatcher
        ) as T
    }
}