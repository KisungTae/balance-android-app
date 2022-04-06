package com.beeswork.balance.ui.balancegamedialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.domain.usecase.balancegame.FetchRandomQuestionsUseCase
import com.beeswork.balance.domain.usecase.balancegame.SaveAnswersUseCase
import com.beeswork.balance.ui.profilebalancegamedialog.ProfileBalanceGameViewModel

class BalanceGameViewModelFactory(
    private val fetchRandomQuestionsUseCase: FetchRandomQuestionsUseCase,
    private val saveAnswersUseCase: SaveAnswersUseCase
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileBalanceGameViewModel(fetchRandomQuestionsUseCase, saveAnswersUseCase) as T
    }
}