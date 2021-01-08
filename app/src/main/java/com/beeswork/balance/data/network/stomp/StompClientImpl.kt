package com.beeswork.balance.data.network.stomp

import com.beeswork.balance.internal.constant.BalanceURL
import com.neovisionaries.ws.client.*
import com.neovisionaries.ws.client.WebSocket
import okhttp3.*
import okio.ByteString


class StompClientImpl : StompClient {

    private val webSocket: WebSocket =
        WebSocketFactory().setConnectionTimeout(50000).createSocket(BalanceURL.WEB_SOCKET_ENDPOINT)

    init {
        setupWebSocketListener()
    }

    private fun setupWebSocketListener() {
        webSocket.addListener(object : WebSocketAdapter() {
            override fun onConnected(
                websocket: WebSocket?,
                headers: MutableMap<String, MutableList<String>>?
            ) {
                println("onConnected")
                super.onConnected(websocket, headers)
            }

            override fun onFrame(websocket: WebSocket?, frame: WebSocketFrame?) {
                println("onFrame")
                println(frame)
                Thread.sleep(10000)
                super.onFrame(websocket, frame)
            }

            override fun onFrameError(
                websocket: WebSocket?,
                cause: WebSocketException?,
                frame: WebSocketFrame?
            ) {
                println("onFrameError")
                super.onFrameError(websocket, cause, frame)
            }

            override fun onConnectError(websocket: WebSocket?, exception: WebSocketException?) {
                println("onConnectError")
                super.onConnectError(websocket, exception)
            }

            override fun onDisconnected(
                websocket: WebSocket?,
                serverCloseFrame: WebSocketFrame?,
                clientCloseFrame: WebSocketFrame?,
                closedByServer: Boolean
            ) {
                println("onDisconnected")
                super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer)
            }

            override fun onUnexpectedError(websocket: WebSocket?, cause: WebSocketException?) {
                super.onUnexpectedError(websocket, cause)
            }
        })
    }

    override fun send() {
        println("stomp client send!!!!!!!!!!!!!!!")
    }

}

