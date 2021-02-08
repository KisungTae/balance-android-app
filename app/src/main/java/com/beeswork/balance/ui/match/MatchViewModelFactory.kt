package com.beeswork.balance.ui.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository

class MatchViewModelFactory(
    private val balanceRepository: BalanceRepository,
    private val matchRepository: MatchRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MatchViewModel(balanceRepository, matchRepository) as T
    }
}