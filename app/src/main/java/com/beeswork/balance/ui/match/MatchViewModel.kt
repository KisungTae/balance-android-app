package com.beeswork.balance.ui.match

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beeswork.balance.data.repository.BalanceRepository
import com.beeswork.balance.internal.Resource
import com.beeswork.balance.internal.lazyDeferred

class MatchViewModel (
    private val balanceRepository: BalanceRepository
): ViewModel() {

    val fetchMatchResource: MutableLiveData<Resource<String>> = balanceRepository.fetchMatchResource

    val matches by lazyDeferred {
        balanceRepository.getMatches()
    }

    fun fetchMatches() {
        balanceRepository.fetchMatches()
    }


}