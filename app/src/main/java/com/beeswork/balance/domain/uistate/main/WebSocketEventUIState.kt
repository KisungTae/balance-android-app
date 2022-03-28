package com.beeswork.balance.domain.uistate.main

import com.beeswork.balance.data.network.service.stomp.WebSocketStatus

data class WebSocketEventUIState(
    val connected: Boolean,
    val shouldLogout: Boolean,
    val exception: Throwable?
)