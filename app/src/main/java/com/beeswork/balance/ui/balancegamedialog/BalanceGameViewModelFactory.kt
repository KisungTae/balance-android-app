package com.beeswork.balance.ui.balancegamedialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.domain.usecase.balancegame.FetchQuestionsUseCase
import com.beeswork.balance.domain.usecase.balancegame.FetchRandomQuestionUseCase
import com.beeswork.balance.domain.usecase.balancegame.SaveAnswersUseCase
import com.beeswork.balance.domain.usecase.card.ClickUseCase
import com.beeswork.balance.domain.usecase.card.LikeUseCase
import com.beeswork.balance.domain.usecase.photo.GetProfilePhotoUrlUseCase
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.internal.mapper.profile.QuestionMapper

class BalanceGameViewModelFactory(
    private val fetchQuestionsUseCase: FetchQuestionsUseCase,
    private val saveAnswersUseCase: SaveAnswersUseCase,
    private val fetchRandomQuestionUseCase: FetchRandomQuestionUseCase,
    private val getProfilePhotoUrlUseCase: GetProfilePhotoUrlUseCase,
    private val likeUseCase: LikeUseCase,
    private val clickUseCase: ClickUseCase,
    private val matchMapper: MatchMapper,
    private val questionMapper: QuestionMapper
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BalanceGameViewModel(
            fetchQuestionsUseCase,
            saveAnswersUseCase,
            fetchRandomQuestionUseCase,
            getProfilePhotoUrlUseCase,
            likeUseCase,
            clickUseCase,
            matchMapper,
            questionMapper
        ) as T
    }
}