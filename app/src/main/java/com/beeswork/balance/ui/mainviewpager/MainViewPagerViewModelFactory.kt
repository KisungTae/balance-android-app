package com.beeswork.balance.ui.mainviewpager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.click.ClickRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.data.network.service.stomp.StompClient
import com.beeswork.balance.internal.mapper.click.ClickMapper
import kotlinx.coroutines.CoroutineDispatcher

class MainViewPagerViewModelFactory(
    private val matchRepository: MatchRepository,
    private val clickRepository: ClickRepository,
    private val clickMapper: ClickMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewPagerViewModel(
            matchRepository,
            clickRepository,
            clickMapper,
            defaultDispatcher
        ) as T
    }
}