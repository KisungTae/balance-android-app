package com.beeswork.balance.data.network.service.stomp

class WebSocketEvent(
    val status: WebSocketStatus,
    val exception: Throwable?
)