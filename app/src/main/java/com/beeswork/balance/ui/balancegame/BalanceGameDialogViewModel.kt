package com.beeswork.balance.ui.balancegame

import androidx.lifecycle.ViewModel
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository

class BalanceGameDialogViewModel(
    private val swipeRepository: SwipeRepository,
    private val profileRepository: ProfileRepository
): ViewModel() {

//    val balanceGame: LiveData<Resource<BalanceGameResponse>> = balanceRepository.balanceGame
//    val clickResponse: LiveData<Resource<ClickResponse>> = balanceRepository.clickResponse

    fun click(swipedId:String, swipeId:Long, answers: Map<Int, Boolean>) {
//        balanceRepository.click(swipedId, swipeId, answers)
    }

    fun swipe(swipeId: Long?, swipedId: String) {
//        balanceRepository.swipe(swipeId, swipedId)
    }
}