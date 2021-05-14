package com.beeswork.balance.ui.mainviewpager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.click.ClickRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.data.network.service.stomp.StompClient

class MainViewPagerViewModelFactory(
    private val matchRepository: MatchRepository,
    private val clickRepository: ClickRepository,
    private val stompClient: StompClient
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewPagerViewModel(matchRepository, clickRepository, stompClient) as T
    }
}