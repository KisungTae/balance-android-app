package com.beeswork.balance.ui.balancegame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.internal.mapper.profile.QuestionMapper

class BalanceGameDialogViewModelFactory(
    private val swipeRepository: SwipeRepository,
    private val profileRepository: ProfileRepository,
    private val matchRepository: MatchRepository,
    private val questionMapper: QuestionMapper
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BalanceGameDialogViewModel(swipeRepository, profileRepository, matchRepository, questionMapper) as T
    }
}