package com.beeswork.balance.data.network.stomp

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.internal.constant.BalanceURL
import com.beeswork.balance.internal.constant.HttpHeader
import com.beeswork.balance.internal.provider.PreferenceProvider
import com.beeswork.balance.internal.safeLet
import com.neovisionaries.ws.client.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class StompClientImpl(
    private val balanceRepository: BalanceRepository,
    private val preferenceProvider: PreferenceProvider,
    private val context: Context
) : StompClient {

    private val mutableWebSocketLifeCycleEvent =
        MutableLiveData<WebSocketLifeCycleEvent>()
    override val webSocketLifeCycleEvent: LiveData<WebSocketLifeCycleEvent>
        get() = mutableWebSocketLifeCycleEvent

//    private var webSocket: WebSocket =
//        WebSocketFactory().createSocket(BalanceURL.WEB_SOCKET_ENDPOINT)


    private var chatId: Long? = null
    private var matchedId: String? = null

    init {
        setupWebSocketListener()
    }


    private fun setupWebSocketListener() {
//        webSocket.addListener(object : WebSocketAdapter() {
//            override fun onConnected(
//                websocket: WebSocket?,
//                headers: MutableMap<String, MutableList<String>>?
//            ) {
//                connectStomp()
//            }
//
//            override fun onFrame(websocket: WebSocket?, frame: WebSocketFrame?) {
//                frame?.let {
//                    val stompFrame = StompFrame.from(frame.payloadText)
//                    when (stompFrame.command) {
//                        StompFrame.Command.CONNECTED -> {
//                            subscribe()
//                        }
//                        StompFrame.Command.MESSAGE -> {
//
//                        }
//                        StompFrame.Command.RECEIPT -> {
//                            safeLet(
//                                stompFrame.message?.chatId,
//                                stompFrame.getMessageId(),
//                                stompFrame.message?.id,
//                                stompFrame.message?.createdAt,
//                            ) { chatId, messageId, id, createdAt ->
//                                CoroutineScope(Dispatchers.IO).launch {
//                                    balanceRepository.syncMessage(chatId, messageId, id, createdAt)
//                                }
//                            }
//                        }
//                        StompFrame.Command.ERROR -> {
//                            mutableWebSocketLifeCycleEvent.postValue(
//                                WebSocketLifeCycleEvent(
//                                    WebSocketLifeCycleEvent.Type.ERROR,
//                                    stompFrame.getError(),
//                                    stompFrame.getErrorMessage()
//                                )
//                            )
//                        }
//                        else -> println("stompframe.command when else here")
//                    }
//                }
//            }
//
//            override fun onFrameError(
//                websocket: WebSocket?,
//                cause: WebSocketException?,
//                frame: WebSocketFrame?
//            ) {
//                println("onFrameError")
//                super.onFrameError(websocket, cause, frame)
//            }
//
//            override fun onConnectError(websocket: WebSocket?, exception: WebSocketException?) {
//                println("onConnectError")
//                super.onConnectError(websocket, exception)
//            }
//
//            override fun onDisconnected(
//                websocket: WebSocket?,
//                serverCloseFrame: WebSocketFrame?,
//                clientCloseFrame: WebSocketFrame?,
//                closedByServer: Boolean
//            ) {
//                println("onDisconnected")
//                mutableWebSocketLifeCycleEvent.postValue(WebSocketLifeCycleEvent.disconnect())
//                super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer)
//            }
//
//            override fun onUnexpectedError(websocket: WebSocket?, cause: WebSocketException?) {
//                println("onUnexpectedError")
//                super.onUnexpectedError(websocket, cause)
//            }
//        })
    }

    private fun connectWebSocket() {
//        if (webSocket.state != WebSocketState.CREATED)
//            webSocket = webSocket.recreate()
//        CoroutineScope(Dispatchers.IO).launch {
//            webSocket.connect()
//        }
    }

    private fun connectStomp() {
//        CoroutineScope(Dispatchers.IO).launch {
//            val headers = mutableMapOf<String, String>()
//            headers[StompHeader.VERSION] = SUPPORTED_VERSIONS
//            headers[StompHeader.HEART_BEAT] = DEFAULT_HEART_BEAT
//            webSocket.sendText(StompFrame(StompFrame.Command.CONNECT, headers).compile())
//        }
    }

    private fun subscribe() {
//        safeLet(chatId, matchedId) { chatId, matchedId ->
//            CoroutineScope(Dispatchers.IO).launch {
//                val headers = stompIdentityHeaders(queueName(chatId), matchedId, chatId)
//                headers[StompHeader.ID] = UUID.randomUUID().toString()
//                headers[StompHeader.ACK] = DEFAULT_ACK
//                headers[StompHeader.AUTO_DELETE] = true.toString()
//                headers[StompHeader.EXCLUSIVE] = false.toString()
//                headers[StompHeader.DURABLE] = true.toString()
//                webSocket.sendText(StompFrame(StompFrame.Command.SUBSCRIBE, headers).compile())
//            }
//        } ?: kotlin.run {
//        }
    }

    override fun connectChat(chatId: Long, matchedId: String) {
        this.chatId = chatId
        this.matchedId = matchedId
        connectWebSocket()
    }

    private fun stompIdentityHeaders(
        destination: String,
        matchedId: String,
        chatId: Long
    ): MutableMap<String, String> {
        val headers = mutableMapOf<String, String>()
        headers[StompHeader.DESTINATION] = destination
        headers[StompHeader.ACCOUNT_ID] = preferenceProvider.getAccountId()
        headers[StompHeader.IDENTITY_TOKEN] = preferenceProvider.getIdentityToken()
        headers[StompHeader.RECIPIENT_ID] = matchedId
        headers[StompHeader.CHAT_ID] = chatId.toString()
        headers[HttpHeader.ACCEPT_LANGUAGE] = Locale.getDefault().toString()
        return headers
    }

    override fun send(chatId: Long, matchedId: String, body: String) {



//        CoroutineScope(Dispatchers.IO).launch {
//            balanceRepository.fetchChatMessages(chatId, matchedId)
//        }

//        if (message.toByteArray().size > MAX_MESSAGE_SIZE) {
//            mutableWebSocketLifeCycleEvent.postValue(
//                WebSocketLifeCycleEvent.error(
//                    null,
//                    context.resources.getString(R.string.chat_message_out_of_size_exception)
//                )
//            )
//            return
//        }
        CoroutineScope(Dispatchers.IO).launch {
            balanceRepository.saveChatMessage(chatId, body)
        }


//        CoroutineScope(Dispatchers.IO).launch {
//            val headers = mutableMapOf<String, String>()
//            headers[StompHeader.IDENTITY_TOKEN] = preferenceProvider.getIdentityToken()
//            headers[StompHeader.DESTINATION] = BalanceURL.STOMP_SEND_ENDPOINT
//            headers[HttpHeader.ACCEPT_LANGUAGE] = Locale.getDefault().toString()
//            headers[StompHeader.RECEIPT] = preferenceProvider.getAccountId()
//            headers[StompHeader.MESSAGE_ID] = balanceRepository.saveMessage(chatId, message).toString()
//            val stompMessage = StompFrame.Message(
//                null,
//                message,
//                preferenceProvider.getAccountId(),
//                matchedId,
//                chatId,
//                null
//            )
//            webSocket.sendText(
//                StompFrame(
//                    StompFrame.Command.SEND,
//                    headers,
//                    stompMessage,
//                    null
//                ).compile()
//            )
//        }
    }

    override fun disconnectChat() {
//        webSocket.disconnect()
    }

    private fun queueName(chatId: Long): String {
        return "/queue/${preferenceProvider.getAccountId()}-$chatId"
    }

    companion object {
        private const val SUPPORTED_VERSIONS = "1.1,1.2"
        private const val DEFAULT_ACK = "auto"
        private const val DEFAULT_HEART_BEAT = "0,0"
        private const val MAX_MESSAGE_SIZE = 1024

    }

}


// TODO: change message in stompframe to message entity
// TODO: lifecycle event error and close socket in subscribe()
// TODO: what happens when exception is thrown in onFrame()
// TODO: before sending, check if subscribtion or connection is open
// TODO: no internet connection then what?
