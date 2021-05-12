package com.beeswork.balance.ui.swipe

import androidx.lifecycle.ViewModel
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository

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