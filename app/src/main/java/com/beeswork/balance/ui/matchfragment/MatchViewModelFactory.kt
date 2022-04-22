package com.beeswork.balance.ui.matchfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.chat.ChatRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.CoroutineDispatcher

class MatchViewModelFactory(
    private val matchRepository: MatchRepository,
    private val preferenceProvider: PreferenceProvider,
    private val matchMapper: MatchMapper,
    private val defaultDispatcher: CoroutineDispatcher
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MatchViewModel(matchRepository, preferenceProvider, matchMapper, defaultDispatcher) as T
    }
}