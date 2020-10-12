package com.beeswork.balance.ui.clicked

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.repository.BalanceRepository
import com.beeswork.balance.ui.chat.ChatViewModel

class ClickedViewModelFactory(
    private val balanceRepository: BalanceRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ClickedViewModel(balanceRepository) as T
    }
}