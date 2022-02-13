package com.beeswork.balance.ui.cardfragment.balancegame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.database.repository.card.CardRepository
import com.beeswork.balance.internal.mapper.profile.QuestionMapper

class SwipeBalanceGameViewModelFactory(
    private val cardRepository: CardRepository,
    private val matchRepository: MatchRepository,
    private val questionMapper: QuestionMapper
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SwipeBalanceGameViewModel(cardRepository, matchRepository, questionMapper) as T
    }
}