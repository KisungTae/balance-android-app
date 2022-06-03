package com.beeswork.balance.domain.uistate.main

import com.beeswork.balance.data.network.service.stomp.WebSocketStatus

data class WebSocketEventUIState(
    val webSocketStatus: WebSocketStatus,
    val shouldLogout: Boolean,
    val exception: Throwable?
)