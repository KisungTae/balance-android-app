package com.beeswork.balance.data.network.service.stomp

import com.beeswork.balance.data.network.api.HttpHeader
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.data.network.response.chat.StompReceiptDTO
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.data.network.response.swipe.SwipeDTO
import com.beeswork.balance.internal.constant.*
import com.beeswork.balance.internal.exception.AccessTokenNotFoundException
import com.beeswork.balance.internal.exception.AccountIdNotFoundException
import com.beeswork.balance.internal.exception.ServerException
import com.beeswork.balance.internal.exception.WebSocketDisconnectedException
import com.beeswork.balance.internal.provider.gson.GsonProvider
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.sync.Mutex
import okhttp3.*
import okio.ByteString
import java.util.*


class StompClientImpl(
    private val okHttpClient: OkHttpClient,
    private val webSocketState: WebSocketState,
    private val preferenceProvider: PreferenceProvider,
    private val ioDispatcher: CoroutineDispatcher,
    private val applicationScope: CoroutineScope
) : StompClient, WebSocketListener() {

    private var socket: WebSocket? = null
    private val outgoingChannel = Channel<String>(Channel.BUFFERED)

    private val swipeChannel = Channel<SwipeDTO>(Channel.BUFFERED)
    val swipeFlow = swipeChannel.consumeAsFlow()

    private val matchChannel = Channel<MatchDTO>(Channel.BUFFERED)
    val matchFlow = matchChannel.consumeAsFlow()

    private val chatMessageChannel = Channel<ChatMessageDTO>(Channel.BUFFERED)
    val chatMessageFlow = chatMessageChannel.consumeAsFlow()

    private val stompReceiptChannel = Channel<StompReceiptDTO>(Channel.BUFFERED)
    val stompReceiptFlow = stompReceiptChannel.consumeAsFlow()

    @ExperimentalCoroutinesApi
    override val webSocketEventChannel = BroadcastChannel<WebSocketEvent>(4)


    init {
        applicationScope.launch {
            outgoingChannel.consumeAsFlow().collect { data ->
                socket?.send(data)
            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun connect(forceToConnect: Boolean) {
        applicationScope.launch {
            if (preferenceProvider.getAccessToken().isNullOrBlank()) {
                webSocketEventChannel.send(WebSocketEvent(WebSocketStatus.ERROR, AccessTokenNotFoundException()))
                socket?.close(1000, null)
                return@launch
            }

            if (webSocketState.isClosed() && forceToConnect) {
                webSocketState.reset()
            }

            if (webSocketState.isConnectableAndSetToConnecting()) {
                val webSocketRequest = Request.Builder()
                    .addHeader(HttpHeader.NO_AUTHENTICATION, true.toString())
                    .url(EndPoint.WEB_SOCKET_ENDPOINT)
                    .build()
                socket = okHttpClient.newWebSocket(webSocketRequest, this@StompClientImpl)
            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun onOpen(webSocket: WebSocket, response: Response) {
        println("override fun onOpen(webSocket: WebSocket, response: Response)")
        applicationScope.launch {
            webSocketState.setSocketStatus(WebSocketStatus.OPEN)
            val accessToken = preferenceProvider.getAccessToken()
            if (accessToken.isNullOrBlank()) {
                webSocketEventChannel.send(WebSocketEvent(WebSocketStatus.ERROR, AccessTokenNotFoundException()))
                socket?.close(1000, null)
            } else {
                val headers = mutableMapOf<String, String>()
                headers[StompHeader.VERSION] = SUPPORTED_VERSIONS
                headers[StompHeader.HEART_BEAT] = DEFAULT_HEART_BEAT
                headers[StompHeader.ACCEPT_LANGUAGE] = Locale.getDefault().toString()
                headers[HttpHeader.ACCESS_TOKEN] = accessToken
                socket?.send(StompFrame(StompFrame.Command.CONNECT, headers, null).compile())
            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun onMessage(webSocket: WebSocket, text: String) {
        applicationScope.launch {
            val stompFrame = StompFrame.from(text)
            println("override fun onMessage(webSocket: WebSocket, text: String): ${stompFrame.command}")
            when (stompFrame.command) {
                StompFrame.Command.CONNECTED -> onConnectedFrameReceived()
                StompFrame.Command.MESSAGE -> onMessageFrameReceived(stompFrame)
                StompFrame.Command.RECEIPT -> onReceiptFrameReceived(stompFrame)
                StompFrame.Command.ERROR -> onErrorFrameReceived(stompFrame)
            }
        }
    }

    @ExperimentalCoroutinesApi
    private suspend fun onConnectedFrameReceived() {
        println("private fun onConnectedFrameReceived()")
        webSocketState.setSocketStatus(WebSocketStatus.STOMP_CONNECTED)
        val accountId = preferenceProvider.getAccountId()
        if (accountId == null) {
            webSocketEventChannel.send(WebSocketEvent(WebSocketStatus.ERROR, AccountIdNotFoundException()))
            socket?.close(1000, null)
            return
        }

        val accessToken = preferenceProvider.getAccessToken()
        if (accessToken.isNullOrBlank()) {
            webSocketEventChannel.send(WebSocketEvent(WebSocketStatus.ERROR, AccessTokenNotFoundException()))
            socket?.close(1000, null)
            return
        }

        applicationScope.launch {
            val headers = mutableMapOf<String, String>()
            headers[StompHeader.DESTINATION] = QUEUE_PREFIX + accountId.toString()
            headers[StompHeader.ACCEPT_LANGUAGE] = Locale.getDefault().toString()
            headers[HttpHeader.ACCESS_TOKEN] = accessToken
            socket?.send(StompFrame(StompFrame.Command.SUBSCRIBE, headers, null).compile())
        }
    }

    private suspend fun onMessageFrameReceived(stompFrame: StompFrame) {
        println("onMessageFrameReceived")
        when (stompFrame.getPushType()) {
            PushType.SWIPE -> {
                val swipeDTO = GsonProvider.gson.fromJson(stompFrame.payload, SwipeDTO::class.java)
                swipeChannel.send(swipeDTO)
            }
            PushType.MATCH -> {
                val matchDTO = GsonProvider.gson.fromJson(stompFrame.payload, MatchDTO::class.java)
                matchChannel.send(matchDTO)
            }
            PushType.CHAT_MESSAGE -> {
                val chatMessageDTO = GsonProvider.gson.fromJson(stompFrame.payload, ChatMessageDTO::class.java)
                chatMessageChannel.send(chatMessageDTO)
            }
        }
    }

    private suspend fun onReceiptFrameReceived(stompFrame: StompFrame) {
        stompFrame.payload?.let { payload ->
            val stompReceiptDTO = GsonProvider.gson.fromJson(payload, StompReceiptDTO::class.java)
            stompReceiptChannel.send(stompReceiptDTO)
        }
    }

    @ExperimentalCoroutinesApi
    private suspend fun onErrorFrameReceived(stompFrame: StompFrame) {
        println("private fun onErrorFrameReceived(stompFrame: StompFrame): ${stompFrame.getError()} - ${stompFrame.getErrorMessage()}")
        val serverException = ServerException(stompFrame.getError(), stompFrame.getErrorMessage())
        val webSocketEvent = WebSocketEvent(WebSocketStatus.ERROR, serverException)
        webSocketEventChannel.send(webSocketEvent)
    }


    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {}

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        println("override fun onClosing(webSocket: WebSocket, code: Int, reason: String)")
        println("onClosing code: $code")
        println("onClosing reason: $reason")
        socket?.close(code, reason)
    }

    @ExperimentalCoroutinesApi
    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        println("onClosed reason: $reason")
        applicationScope.launch {
            webSocketState.setSocketStatus(WebSocketStatus.CLOSED)
            webSocketEventChannel.send(WebSocketEvent(WebSocketStatus.CLOSED, null))
        }
    }

    @ExperimentalCoroutinesApi
    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        println("override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?)")
        applicationScope.launch {
            webSocketState.setSocketStatus(WebSocketStatus.CLOSED)
            webSocketEventChannel.send(WebSocketEvent(WebSocketStatus.CLOSED, t))
        }
    }

    @ExperimentalCoroutinesApi
    override suspend fun sendChatMessage(chatMessageDTO: ChatMessageDTO): Resource<EmptyResponse> {
        println("private suspend fun sendChatMessage(chatMessageDTO: ChatMessageDTO)")
        if (webSocketState.isClosed()) {
            return Resource.error(WebSocketDisconnectedException())
        }

        val accessToken = preferenceProvider.getAccessToken()
        if (accessToken.isNullOrBlank()) {
            webSocketEventChannel.send(WebSocketEvent(WebSocketStatus.ERROR, AccessTokenNotFoundException()))
            socket?.close(1000, null)
            return Resource.error(AccessTokenNotFoundException())
        }

        if (webSocketState.isStompConnected()) {
            val headers = mutableMapOf<String, String>()
            headers[StompHeader.DESTINATION] = EndPoint.STOMP_SEND_ENDPOINT
            headers[StompHeader.RECEIPT] = chatMessageDTO.tag.toString()
            headers[StompHeader.ACCEPT_LANGUAGE] = Locale.getDefault().toString()
            headers[HttpHeader.ACCESS_TOKEN] = accessToken
            val stompFrame = StompFrame(StompFrame.Command.SEND, headers, GsonProvider.gson.toJson(chatMessageDTO))
            outgoingChannel.send(stompFrame.compile())
        }
        return Resource.success(EmptyResponse())
    }

    override fun disconnect() {
        println("override suspend fun disconnect()")
        applicationScope.launch {
            webSocketState.setDisconnectedByUser(true)
            socket?.close(1000, null)
        }
    }

    companion object {
        private const val SUPPORTED_VERSIONS = "1.1,1.2"
        private const val DEFAULT_HEART_BEAT = "0,0"
        private const val QUEUE_PREFIX = "/queue/"
    }
}


// TODO: lifecycle event error and close socket in subscribe()
// TODO: what happens when exception is thrown in onFrame()