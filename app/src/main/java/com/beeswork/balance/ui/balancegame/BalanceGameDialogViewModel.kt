package com.beeswork.balance.ui.balancegame

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.data.network.response.BalanceGameResponse
import com.beeswork.balance.data.network.response.ClickResponse
import com.beeswork.balance.data.network.response.Resource

class BalanceGameDialogViewModel(
    private val balanceRepository: BalanceRepository
): ViewModel() {

    val balanceGame: LiveData<Resource<BalanceGameResponse>> = balanceRepository.balanceGame
    val clickResponse: LiveData<Resource<ClickResponse>> = balanceRepository.clickResponse

    fun click(swipedId:String, swipeId:Long, answers: Map<Int, Boolean>) {
        balanceRepository.click(swipedId, swipeId, answers)
    }

    fun swipe(swipeId: Long?, swipedId: String) {
        balanceRepository.swipe(swipeId, swipedId)
    }
}