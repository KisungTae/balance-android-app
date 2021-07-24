package com.beeswork.balance.ui.mainactivity

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.main.MainRepository
import com.beeswork.balance.data.network.service.stomp.StompClient
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel(
    private val mainRepository: MainRepository
) : BaseViewModel() {

    //  TODO: change livedata to channel consumeAsFlow, and validateAccount() in onEach()
//    val webSocketEventLiveData = stompClient.webSocketEventLiveData

    val webSocketEventLiveData by viewModelLazyDeferred {
        mainRepository.getWebSocketEventFlow().asLiveData()
    }


    fun connectStomp() {
        viewModelScope.launch { mainRepository.connectStomp() }
    }

    fun disconnectStomp() {
        viewModelScope.launch { mainRepository.disconnectStomp() }
    }

}