package com.beeswork.balance.ui.mainviewpager

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.click.ClickRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.internal.mapper.click.ClickMapper
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map

class MainViewPagerViewModel(
    private val matchRepository: MatchRepository,
    private val clickRepository: ClickRepository,
    private val clickMapper: ClickMapper,
    private val defaultDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    val clickCountLiveData by viewModelLazyDeferred {
        clickRepository.clickCountFlow.asLiveData()
    }

    val newClickLiveData by viewModelLazyDeferred {
        clickRepository.newClickFlow.map { click ->
            clickMapper.toClickDomain(click)
        }.asLiveData(viewModelScope.coroutineContext + defaultDispatcher)
    }





    val unreadMatchCount by viewModelLazyDeferred {
        matchRepository.getUnreadMatchCountFlow().asLiveData()
    }


}