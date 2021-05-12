package com.beeswork.balance.ui.swipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository

class SwipeFilterDialogViewModelFactory(
    private val swipeRepository: SwipeRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SwipeFilterDialogViewModel(swipeRepository) as T
    }
}