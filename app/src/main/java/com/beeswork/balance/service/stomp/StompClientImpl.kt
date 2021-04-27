package com.beeswork.balance.service.stomp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.internal.constant.EndPoint
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.constant.PushType
import com.beeswork.balance.internal.constant.StompHeader
import com.beeswork.balance.internal.exception.NoInternetConnectivityException
import com.beeswork.balance.internal.provider.gson.GsonProvider
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import okhttp3.*
import okio.ByteString
import java.util.*
import kotlin.random.Random


class StompClientImpl(
    private val scope: CoroutineScope,
    private val okHttpClient: OkHttpClient,
    private val preferenceProvider: PreferenceProvider
) : StompClient, WebSocketListener() {

    enum class SocketStatus {
        CONNECTING,
        OPEN,
        CLOSED
    }

    private var socket: WebSocket? = null
    private var outgoing = Channel<String>()
    private var isSocketOpen: Boolean = false
    private var subscriptionId = 8
    private var socketStatus = SocketStatus.CLOSED

    private var chatMessageReceiptChannel = Channel<ChatMessageDTO>()
    override val chatMessageReceiptFlow = chatMessageReceiptChannel.consumeAsFlow()

    private var chatMessageReceivedChannel = Channel<ChatMessageDTO>()
    override val chatMessageReceivedFlow = chatMessageReceivedChannel.consumeAsFlow()

    private var matchedChannel = Channel<MatchDTO>()
    override val matchedFlow = matchedChannel.consumeAsFlow()

//    private var clickedChannel = Channel<Clicked>


    private val _webSocketEventLiveData = MutableLiveData<WebSocketEvent>()
    override val webSocketEventLiveData: LiveData<WebSocketEvent> get() = _webSocketEventLiveData

    init {
        scope.launch {
            outgoing.consumeEach { socket?.send(it) }
        }
    }


    override fun onOpen(webSocket: WebSocket, response: Response) {
        println("onOpen")
        socketStatus = SocketStatus.OPEN
        connectToStomp()
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        println("onMessage text: ")
        println(text)
        val stompFrame = StompFrame.from(text)
        when (stompFrame.command) {
            StompFrame.Command.CONNECTED -> subscribeToQueue()
            StompFrame.Command.MESSAGE -> onMessageFrameReceived(stompFrame)
            StompFrame.Command.RECEIPT -> onReceiptFrameReceived(stompFrame)
            StompFrame.Command.ERROR -> onErrorFrameReceived(stompFrame)
            else -> println("stompframe.command when else here")
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        println("onMessage bytes")
//            scope.launch(Dispatchers.IO) {
//                incoming.send(RawData(bytes.toString()))
//            }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        socketStatus = SocketStatus.CLOSED
        println("onClosing")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        socketStatus = SocketStatus.CLOSED
        println("onClosed")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        println("onFailure!!!!!")
        socketStatus = SocketStatus.CLOSED
        val error: String? = when (t) {
            is NoInternetConnectivityException -> ExceptionCode.NO_INTERNET_CONNECTIVITY_EXCEPTION
            else -> null
        }
        _webSocketEventLiveData.postValue(WebSocketEvent.error(error, null))
    }

    override fun connect() {
        if (socketStatus == SocketStatus.CLOSED) {
            socketStatus = SocketStatus.CONNECTING
            socket = okHttpClient.newWebSocket(Request.Builder().url(EndPoint.WEB_SOCKET_ENDPOINT).build(), this)
        }
    }

    private fun onMessageFrameReceived(stompFrame: StompFrame) {
        when (stompFrame.getPushType()) {
            PushType.CHAT_MESSAGE -> scope.launch {
                chatMessageReceivedChannel.send(
                    GsonProvider.gson.fromJson(
                        stompFrame.payload,
                        ChatMessageDTO::class.java
                    )
                )
            }
            PushType.CLICKED -> scope.launch {

            }
            PushType.MATCHED -> scope.launch {

            }
        }
    }

    private fun onReceiptFrameReceived(stompFrame: StompFrame) {
        stompFrame.payload?.let {
            scope.launch {
                val chatMessageDTO = GsonProvider.gson.fromJson(it, ChatMessageDTO::class.java)
                chatMessageDTO.key = stompFrame.getReceiptId()
                chatMessageReceiptChannel.send(chatMessageDTO)
            }
        }
    }

    private fun onErrorFrameReceived(stompFrame: StompFrame) {
        _webSocketEventLiveData.postValue(
            WebSocketEvent.error(
                stompFrame.getError(),
                if (stompFrame.getError() == null) null else stompFrame.getErrorMessage()
            )
        )
    }

    override fun sendChatMessage(key: Long, chatId: Long, matchedId: UUID, body: String) {
        scope.launch {
            if (socketStatus == SocketStatus.CLOSED) connect()
            while (socketStatus == SocketStatus.CONNECTING) { }

            if (socketStatus == SocketStatus.OPEN) {
                val headers = mutableMapOf<String, String>()
                headers[StompHeader.DESTINATION] = EndPoint.STOMP_SEND_ENDPOINT
                headers[StompHeader.ACCOUNT_ID] = "${preferenceProvider.getAccountId()?.toString()}"
                headers[StompHeader.IDENTITY_TOKEN] = "${preferenceProvider.getIdentityToken()?.toString()}"
                headers[StompHeader.RECEIPT] = key.toString()
                headers[StompHeader.ACCEPT_LANGUAGE] = Locale.getDefault().toString()
                val chatMessageDTO = ChatMessageDTO(null, null, chatId, body, null, matchedId)
                val stompFrame = StompFrame(StompFrame.Command.SEND, headers, GsonProvider.gson.toJson(chatMessageDTO))
                outgoing.send(stompFrame.compile())
            } else if (socketStatus == SocketStatus.CLOSED) {
                chatMessageReceiptChannel.send(ChatMessageDTO(key, null, chatId, null, null, null))
            }
        }
    }

    private fun connectToStomp() {
        scope.launch {
            val headers = mutableMapOf<String, String>()
            headers[StompHeader.VERSION] = SUPPORTED_VERSIONS
            headers[StompHeader.HEART_BEAT] = DEFAULT_HEART_BEAT
            socket?.send(StompFrame(StompFrame.Command.CONNECT, headers, null).compile())
        }
    }

    private fun subscribeToQueue() {
        scope.launch {
            val headers = mutableMapOf<String, String>()
            headers[StompHeader.DESTINATION] = getDestination(preferenceProvider.getAccountId())
            headers[StompHeader.ID] = subscriptionId.toString()
            headers[StompHeader.IDENTITY_TOKEN] = "${preferenceProvider.getIdentityToken()?.toString()}"
            headers[StompHeader.ACK] = DEFAULT_ACK
            socket?.send(StompFrame(StompFrame.Command.SUBSCRIBE, headers, null).compile())
        }
    }

    private fun getDestination(id: UUID?): String {
        return "/queue/${id?.toString()}"
    }


    private fun setupWebSocketListener() {
//        webSocket.addListener(object : WebSocketAdapter() {
//            override fun onConnected(
//                websocket: WebSocket?,
//                headers: MutableMap<Str
        //                ing, MutableList<String>>?
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


    private fun stompIdentityHeaders(): MutableMap<String, String> {
        val headers = mutableMapOf<String, String>()
        headers[StompHeader.DESTINATION] = "/queue/${preferenceProvider.getAccountId()?.toString()}"
        headers[StompHeader.ID] = "${UUID.randomUUID()}"
//        headers[StompHeader.IDENTITY_TOKEN] = "${preferenceProvider.getIdentityToken()?.toString()}"
//        headers[HttpHeader.ACCEPT_LANGUAGE] = Locale.getDefault().toString()
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
//            balanceRepository.saveChatMessage(chatId, body)
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

    override fun disconnect() {
//        webSocket.disconnect()
    }

    private fun queueName(chatId: Long): String {
//        return "/queue/${preferenceProvider.getAccountId1()}-$chatId"
        return ""
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

// TODO: send chatmessage and then if successful, then check active of chat set it to true
// TODO: when send if not connected or web scoket closed, then push the message to buffer and when connected then clear buffer
// TODO: init outoing launch in connect() or init()