package com.beeswork.balance.ui.chatfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.chat.ChatRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.domain.usecase.chat.ResendChatMessageUseCase
import com.beeswork.balance.domain.usecase.chat.SendChatMessageUseCase
import com.beeswork.balance.internal.mapper.chat.ChatMessageMapper
import com.beeswork.balance.internal.mapper.match.MatchMapper
import kotlinx.coroutines.CoroutineDispatcher
import java.util.*

class ChatViewModelFactory(
    private val chatId: UUID,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val resendChatMessageUseCase: ResendChatMessageUseCase,
    private val chatRepository: ChatRepository,
    private val matchRepository: MatchRepository,
    private val chatMessageMapper: ChatMessageMapper,
    private val matchMapper: MatchMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatViewModel(
            chatId,
            sendChatMessageUseCase,
            resendChatMessageUseCase,
            chatRepository,
            matchRepository,
            chatMessageMapper,
            matchMapper,
            defaultDispatcher
        ) as T
    }
}