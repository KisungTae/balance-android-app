package com.beeswork.balance.data.network.stomp

import okhttp3.*
import okio.ByteString


class StompClientImpl {

    private val okHttpClient = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun createWebSocketConnection() {
        val requestBuilder = Request.Builder().url("ws://localhost:8080/chat")
        webSocket = okHttpClient.newWebSocket(requestBuilder.build(), object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
            }
        })
    }


}



// TODO: okhttpconnection interceptor and how to handle internet connectivity exception
