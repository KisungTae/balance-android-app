package com.beeswork.balance.data.network.service.stomp

import com.beeswork.balance.internal.constant.EndPoint
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketAdapter
import com.neovisionaries.ws.client.WebSocketException
import com.neovisionaries.ws.client.WebSocketFactory
import kotlinx.coroutines.CoroutineScope
import okhttp3.OkHttpClient

class WebSocketClientImpl(
    private val applicationScope: CoroutineScope,
    private val preferenceProvider: PreferenceProvider
) : WebSocketClient {

    private val webSocketFactory = WebSocketFactory()
    private lateinit var webSocket: WebSocket

    override suspend fun connect() {
        println("override suspend fun connect() {")
        webSocket = webSocketFactory.setConnectionTimeout(10000)
            .createSocket(EndPoint.WEB_SOCKET_ENDPOINT)
            .addListener(object : WebSocketAdapter() {
                override fun onConnected(websocket: WebSocket?, headers: MutableMap<String, MutableList<String>>?) {
                    println("override fun onConnected")
                    super.onConnected(websocket, headers)
                }

                override fun onConnectError(websocket: WebSocket?, exception: WebSocketException?) {
                    println("override fun onConnectError")
                    super.onConnectError(websocket, exception)
                }

                override fun onSendingHandshake(websocket: WebSocket?, requestLine: String?, headers: MutableList<Array<String>>?) {
                    println("onSendingHandshake")
                    println(requestLine)
                    super.onSendingHandshake(websocket, requestLine, headers)
                }
            }).connect()
    }


}