package com.beeswork.balance.data.network.stomp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.internal.Resource
import com.beeswork.balance.internal.constant.BalanceURL
import com.beeswork.balance.internal.constant.HttpHeader
import com.beeswork.balance.internal.provider.PreferenceProvider
import com.google.gson.JsonObject
import com.neovisionaries.ws.client.*
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.threeten.bp.OffsetDateTime
import java.util.*


class StompClientImpl(
    private val balanceRepository: BalanceRepository,
    private val preferenceProvider: PreferenceProvider
) : StompClient {
    private val mutableWebSocketLifeCycleEvent = MutableLiveData<Resource<WebSocketLifeCycleEvent>>()
    override val webSocketLifeCycleEvent: LiveData<Resource<WebSocketLifeCycleEvent>>
        get() = mutableWebSocketLifeCycleEvent

    private val mutableStompFrame = MutableLiveData<Resource<StompFrame>>()
    override val stompFrame: LiveData<Resource<StompFrame>>
        get() = mutableStompFrame

    private val webSocket: WebSocket = WebSocketFactory().createSocket(BalanceURL.WEB_SOCKET_ENDPOINT)

    init {
        setupWebSocketListener()
        connectWebSocket()
    }


    private fun setupWebSocketListener() {
        webSocket.addListener(object : WebSocketAdapter() {
            override fun onConnected(
                websocket: WebSocket?,
                headers: MutableMap<String, MutableList<String>>?
            ) {
                connect()
                println("onConnected")
            }

            override fun onFrame(websocket: WebSocket?, frame: WebSocketFrame?) {
                frame?.let {
                    println("onFrame")


                    println(frame.payloadText)

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

    private fun connectWebSocket() {
        CoroutineScope(Dispatchers.IO).launch {
            webSocket.connect()
        }
    }

    private fun connect() {
        CoroutineScope(Dispatchers.IO).launch {
            val headers = mutableMapOf<String, String>()
            headers[StompHeader.VERSION] = SUPPORTED_VERSIONS
            headers[StompHeader.HEART_BEAT] = DEFAULT_HEART_BEAT
            webSocket.sendText(StompFrame(StompFrame.Command.CONNECT, headers, null).compile())
        }
    }

    override fun subscribe(chatId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val headers = mutableMapOf<String, String>()
            headers[StompHeader.ID] = UUID.randomUUID().toString()
            headers[StompHeader.DESTINATION] = queueName(chatId)
            headers[StompHeader.ACK] = DEFAULT_ACK
            headers[StompHeader.AUTO_DELETE] = true.toString()
            headers[StompHeader.EXCLUSIVE] = false.toString()
            headers[StompHeader.DURABLE] = true.toString()
            headers[HttpHeader.ACCEPT_LANGUAGE] = Locale.getDefault().language
            webSocket.sendText(StompFrame(StompFrame.Command.SUBSCRIBE, headers, null).compile())
        }
    }

    override fun send(chatId: Long, message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val headers = mutableMapOf<String, String>()
            headers[StompHeader.DESTINATION] = BalanceURL.STOMP_SEND_ENDPOINT
//            headers[StompHeader.RECIPIENT_ID] = matchedId
            headers[StompHeader.CHAT_ID] = chatId.toString()

            val json = JSONObject()
            json.put("message", message)
            json.put("createdAt", OffsetDateTime.now().toString())

            println(json.toString())

//            webSocket.sendText(StompFrame(StompFrame.Command.SEND, headers, json.toString()).compile())
        }
    }



    private fun queueName(chatId: Long): String {
        return "/queue/${preferenceProvider.getAccountId()}-$chatId"
    }


    companion object {
        private const val SUPPORTED_VERSIONS = "1.1,1.2"
        private const val DEFAULT_ACK = "auto"
        private const val DEFAULT_HEART_BEAT = "0,0"

    }

}

