package com.beeswork.balance.ui.click

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import com.beeswork.balance.data.database.repository.click.ClickRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.internal.mapper.click.ClickMapper
import kotlinx.coroutines.CoroutineDispatcher

class ClickViewModelFactory(
    private val clickRepository: ClickRepository,
    private val matchRepository: MatchRepository,
    private val clickMapper: ClickMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ClickViewModel(clickRepository, matchRepository, clickMapper, defaultDispatcher) as T
    }
}