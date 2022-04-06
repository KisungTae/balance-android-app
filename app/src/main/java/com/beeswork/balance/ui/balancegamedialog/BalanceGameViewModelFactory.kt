package com.beeswork.balance.ui.balancegamedialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.domain.usecase.balancegame.FetchQuestionsUseCase
import com.beeswork.balance.domain.usecase.balancegame.SaveAnswersUseCase
import com.beeswork.balance.internal.mapper.profile.QuestionMapper

class BalanceGameViewModelFactory(
    private val fetchQuestionsUseCase: FetchQuestionsUseCase,
    private val saveAnswersUseCase: SaveAnswersUseCase,
    private val questionMapper: QuestionMapper
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BalanceGameViewModel(fetchQuestionsUseCase, saveAnswersUseCase, questionMapper) as T
    }
}