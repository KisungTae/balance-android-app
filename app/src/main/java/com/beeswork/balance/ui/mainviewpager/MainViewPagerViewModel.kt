package com.beeswork.balance.ui.mainviewpager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.beeswork.balance.data.database.repository.click.ClickRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.data.network.service.stomp.StompClient
import com.beeswork.balance.internal.util.lazyDeferred

class MainViewPagerViewModel(
    private val matchRepository: MatchRepository,
    private val clickRepository: ClickRepository,
    private val stompClient: StompClient
) : ViewModel() {

    val webSocketEventLiveData = stompClient.webSocketEventLiveData

    val unreadMatchCount by lazyDeferred {
        matchRepository.getUnreadMatchCount().asLiveData()
    }

    val clickCount by lazyDeferred {
        clickRepository.getClickCount().asLiveData()
    }

    fun connectStomp() {
        stompClient.connect()
    }

    fun disconnectStomp() {
        stompClient.disconnect()
    }


}