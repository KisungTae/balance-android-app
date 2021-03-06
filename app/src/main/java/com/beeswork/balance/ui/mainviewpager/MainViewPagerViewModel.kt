package com.beeswork.balance.ui.mainviewpager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.click.ClickRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.data.network.service.stomp.StompClient
import com.beeswork.balance.internal.util.lazyDeferred
import kotlinx.coroutines.launch

class MainViewPagerViewModel(
    private val matchRepository: MatchRepository,
    private val clickRepository: ClickRepository
) : ViewModel() {

    val unreadMatchCount by lazyDeferred {
        matchRepository.getUnreadMatchCountFlow().asLiveData()
    }

    val clickCount by lazyDeferred {
        clickRepository.getClickCountFlow().asLiveData()
    }




}