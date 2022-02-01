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
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.launch

class MainViewPagerViewModel(
    private val matchRepository: MatchRepository,
    private val clickRepository: ClickRepository
) : BaseViewModel() {

    //    val newClickLiveData by viewModelLazyDeferred {
//        clickRepository.newClickInvalidationFlow.map { click -> }
//    }
//
//    val clickPageInvalidationLiveData by viewModelLazyDeferred {
//        clickRepository.newClickInvalidationFlow.map { click ->
//            if (click != null) {
//                clickMapper.toClickDomain(click)
//            } else {
//                null
//            }
//        }.asLiveData(viewModelScope.coroutineContext + defaultDispatcher)
//    }

    val unreadMatchCount by viewModelLazyDeferred {
        matchRepository.getUnreadMatchCountFlow().asLiveData()
    }

    val clickCount by viewModelLazyDeferred {
        clickRepository.getClickCountFlow().asLiveData()
    }
}