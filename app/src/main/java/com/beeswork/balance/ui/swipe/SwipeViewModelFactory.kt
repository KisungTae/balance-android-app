package com.beeswork.balance.ui.swipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.repository.BalanceRepository

class SwipeViewModelFactory(
    private val balanceRepository: BalanceRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SwipeViewModel(balanceRepository) as T
    }
}