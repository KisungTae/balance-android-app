package com.beeswork.balance.ui.chatfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.chat.ChatRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.internal.mapper.chat.ChatMessageMapper
import com.beeswork.balance.internal.mapper.match.MatchMapper
import kotlinx.coroutines.CoroutineDispatcher
import java.util.*

class ChatViewModelFactory(
    private val chatViewModelFactoryParam: ChatViewModelFactoryParam,
    private val chatRepository: ChatRepository,
    private val matchRepository: MatchRepository,
    private val chatMessageMapper: ChatMessageMapper,
    private val matchMapper: MatchMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatViewModel(
            chatViewModelFactoryParam.chatId,
            chatViewModelFactoryParam.swipedId,
            chatRepository,
            matchRepository,
            chatMessageMapper,
            matchMapper,
            defaultDispatcher
        ) as T
    }
}