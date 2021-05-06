package com.beeswork.balance.ui.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.internal.mapper.match.MatchMapper

class MatchViewModelFactory(
    private val matchRepository: MatchRepository,
    private val matchMapper: MatchMapper
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MatchViewModel(matchRepository, matchMapper) as T
    }
}