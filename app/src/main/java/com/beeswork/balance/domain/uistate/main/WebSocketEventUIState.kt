package com.beeswork.balance.domain.uistate.main

data class WebSocketEventUIState(
    val shouldLogout: Boolean,
    val exception: Throwable?
)