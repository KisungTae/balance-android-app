package com.beeswork.balance.ui.swipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.beeswork.balance.data.network.response.CardResponse
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.data.network.response.BalanceGameResponse
import com.beeswork.balance.internal.Resource
import com.beeswork.balance.internal.lazyDeferred

class SwipeViewModel(
    private val balanceRepository: BalanceRepository
): ViewModel() {

    val cards: LiveData<Resource<List<CardResponse>>> = balanceRepository.cards
    val balanceGame: LiveData<Resource<BalanceGameResponse>> = balanceRepository.balanceGame

    val clickedCount by lazyDeferred {
        balanceRepository.getClickedCount()
    }

    val unreadMessageCount by lazyDeferred {
        balanceRepository.getUnreadMessageCount()
    }

    fun fetchCards() {
        balanceRepository.fetchCards()
    }

    fun swipe(swipeId: String) {
        balanceRepository.swipe(swipeId)
    }

    fun click(swipedId:String, swipeId:Long, answers: Map<Long, Boolean>) {
        balanceRepository.click(swipedId, swipeId, answers)
    }

    fun fetchMatches() {
        balanceRepository.fetchMatches()
    }

    fun fetchClickedList() {
        balanceRepository.fetchClickedList()
    }
}