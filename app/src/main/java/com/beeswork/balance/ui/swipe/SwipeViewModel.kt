package com.beeswork.balance.ui.swipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.beeswork.balance.data.network.response.BalanceGame
import com.beeswork.balance.data.network.response.Card
import com.beeswork.balance.data.repository.BalanceRepository
import com.beeswork.balance.internal.Resource

class SwipeViewModel(
    private val balanceRepository: BalanceRepository
): ViewModel() {

    val cards: LiveData<Resource<List<Card>>> = balanceRepository.cards
    val balanceGame: LiveData<Resource<BalanceGame>> = balanceRepository.balanceGame

    fun fetchCards() {
        balanceRepository.fetchCards()
    }

    fun swipe(swipeId: String) {
        balanceRepository.swipe(swipeId)
    }

    fun click(swipedId:String, swipeId:Long) {
        balanceRepository.click(swipedId, swipeId)
    }
}