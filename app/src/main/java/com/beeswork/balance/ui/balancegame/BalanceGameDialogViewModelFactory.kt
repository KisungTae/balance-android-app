package com.beeswork.balance.ui.balancegame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository

class BalanceGameDialogViewModelFactory(
    private val swipeRepository: SwipeRepository,
    private val profileRepository: ProfileRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BalanceGameDialogViewModel(swipeRepository, profileRepository) as T
    }
}