package com.beeswork.balance.ui.chatfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.chat.ChatRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.internal.mapper.chat.ChatMessageMapper
import kotlinx.coroutines.CoroutineDispatcher

class ChatViewModelFactory(
    private val chatViewModelFactoryParameter: ChatViewModelFactoryParameter,
    private val chatRepository: ChatRepository,
    private val matchRepository: MatchRepository,
    private val chatMessageMapper: ChatMessageMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatViewModel(
            chatViewModelFactoryParameter.chatId,
            chatViewModelFactoryParameter.swipedId,
            chatRepository,
            matchRepository,
            chatMessageMapper,
            defaultDispatcher
        ) as T
    }
}