package com.beeswork.balance.ui.swipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.beeswork.balance.data.network.response.CardResponse
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.util.lazyDeferred

class SwipeViewModel(
    private val swipeRepository: SwipeRepository
): ViewModel() {

//    val cards: LiveData<Resource<List<CardResponse>>> = balanceRepository.cards

//    val clickedCount by lazyDeferred {
//        balanceRepository.getClickedCount()
//    }


    fun fetchCards(reset: Boolean) {
//        balanceRepository.fetchCards(reset)
    }

    fun swipe(swipeId: String) {
//        balanceRepository.swipe(null, swipeId)
    }

    fun fetchMatches() {
//        balanceRepository.fetchMatches()
    }

    fun fetchClickedList() {
//        balanceRepository.fetchClickedList()
    }
}