package com.beeswork.balance.ui.match

import androidx.lifecycle.ViewModel
import com.beeswork.balance.data.repository.BalanceRepository
import com.beeswork.balance.internal.lazyDeferred

class MatchViewModel (
    private val balanceRepository: BalanceRepository
): ViewModel() {

    val matches by lazyDeferred {
        balanceRepository.getMatches()
    }

}