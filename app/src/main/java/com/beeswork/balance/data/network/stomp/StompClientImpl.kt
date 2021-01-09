package com.beeswork.balance.data.network.stomp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.beeswork.balance.internal.Resource
import com.beeswork.balance.internal.constant.BalanceURL
import com.neovisionaries.ws.client.*
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.*


class StompClientImpl : StompClient {
    private val mutableWebSocketLifeCycleEvent = MutableLiveData<Resource<WebSocketLifeCycleEvent>>()
    override val webSocketLifeCycleEvent: LiveData<Resource<WebSocketLifeCycleEvent>>
        get() = mutableWebSocketLifeCycleEvent

    private val mutableStompFrame = MutableLiveData<Resource<StompFrame>>()
    override val stompFrame: LiveData<Resource<StompFrame>>
        get() = mutableStompFrame

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
            }

            override fun onFrame(websocket: WebSocket?, frame: WebSocketFrame?) {
                frame?.let {

                }
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

    override fun subscribe(path: String) {
        val headers = mutableMapOf<String, String>()
        headers[StompHeader.ID] = UUID.randomUUID().toString()
        headers[StompHeader.DESTINATION] = path
        headers[StompHeader.ACK] = DEFAULT_ACK
        webSocket.sendText(StompFrame(StompFrame.Command.SUBSCRIBE, headers, null).compile())
    }

    override fun send() {
        println("stomp client send!!!!!!!!!!!!!!!")
    }


    companion object {
        const val SUPPORTED_VERSIONS = "1.1,1.2"
        const val DEFAULT_ACK = "auto"
    }

}

