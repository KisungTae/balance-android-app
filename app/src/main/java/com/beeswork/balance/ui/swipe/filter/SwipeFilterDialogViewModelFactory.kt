package com.beeswork.balance.ui.swipe.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.internal.mapper.swipe.SwipeFilterMapper

class SwipeFilterDialogViewModelFactory(
    private val swipeRepository: SwipeRepository,
    private val swipeFilterMapper: SwipeFilterMapper
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SwipeFilterDialogViewModel(swipeRepository, swipeFilterMapper) as T
    }
}