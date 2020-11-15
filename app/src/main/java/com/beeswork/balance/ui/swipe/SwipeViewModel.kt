package com.beeswork.balance.ui.swipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.beeswork.balance.data.database.entity.Click
import com.beeswork.balance.data.network.response.CardResponse
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.data.network.response.BalanceGameResponse
import com.beeswork.balance.data.network.response.ClickResponse
import com.beeswork.balance.internal.Resource
import com.beeswork.balance.internal.lazyDeferred

class SwipeViewModel(
    private val balanceRepository: BalanceRepository
): ViewModel() {

    val cards: LiveData<Resource<List<CardResponse>>> = balanceRepository.cards

    val clickedCount by lazyDeferred {
        balanceRepository.getClickedCount()
    }

    val unreadMessageCount by lazyDeferred {
        balanceRepository.getUnreadMessageCount()
    }

    fun fetchCards(reset: Boolean) {
        balanceRepository.fetchCards(reset)
    }

    fun swipe(swipeId: String) {
        balanceRepository.swipe(null, swipeId)
    }

    fun fetchMatches() {
        balanceRepository.fetchMatches()
    }

    fun fetchClickedList() {
        balanceRepository.fetchClickedList()
    }
}