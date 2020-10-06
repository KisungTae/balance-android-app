package com.beeswork.balance.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.repository.BalanceRepository

class ChatViewModelFactory(
    private val matchId: Int,
    private val balanceRepository: BalanceRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatViewModel(matchId, balanceRepository) as T
    }
}