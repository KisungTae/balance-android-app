package com.beeswork.balance.ui.profilefragment.balancegame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.internal.mapper.profile.QuestionMapper

class ProfileBalanceGameViewModelFactory(
    private val profileRepository: ProfileRepository,
    private val questionMapper: QuestionMapper
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileBalanceGameViewModel(profileRepository, questionMapper) as T
    }
}