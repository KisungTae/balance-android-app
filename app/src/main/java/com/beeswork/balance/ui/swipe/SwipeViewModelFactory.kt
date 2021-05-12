package com.beeswork.balance.ui.swipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository

class SwipeViewModelFactory(
    private val swipeRepository: SwipeRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SwipeViewModel(swipeRepository) as T
    }
}