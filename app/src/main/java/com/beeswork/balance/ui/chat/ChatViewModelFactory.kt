package com.beeswork.balance.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.service.stomp.StompClient

class ChatViewModelFactory(
    private val chatId: Long,
    private val balanceRepository: BalanceRepository,
    private val stompClient: StompClient
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatViewModel(
            chatId,
            balanceRepository,
            stompClient
        ) as T
    }
}