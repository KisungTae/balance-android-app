package com.beeswork.balance.ui.mainactivity

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.click.ClickRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.data.network.service.stomp.StompClient
import com.beeswork.balance.internal.util.lazyDeferred
import com.beeswork.balance.ui.account.BaseViewModel
import kotlinx.coroutines.launch

class MainViewModel(
    private val stompClient: StompClient
) : BaseViewModel() {

    //  TODO: change livedata to channel consumeAsFlow, and validateAccount() in onEach()
    val webSocketEventLiveData = stompClient.webSocketEventLiveData

    fun connectStomp() {
        stompClient.connect()
    }

    fun disconnectStomp() {
        stompClient.disconnect()
    }

}