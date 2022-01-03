package com.beeswork.balance.data.network.service.stomp

import com.beeswork.balance.data.database.repository.chat.ChatRepository
import com.beeswork.balance.data.database.repository.click.ClickRepository
import com.beeswork.balance.data.database.repository.login.LoginRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.network.api.HttpHeader
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.data.network.response.chat.ChatMessageReceiptDTO
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.data.network.response.click.ClickDTO
import com.beeswork.balance.internal.constant.*
import com.beeswork.balance.internal.exception.NoInternetConnectivityException
import com.beeswork.balance.internal.provider.gson.GsonProvider
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.*
import okio.ByteString
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue


class StompClientImpl(
    private val applicationScope: CoroutineScope,
    private val okHttpClient: OkHttpClient,
    private val chatRepository: ChatRepository,
    private val clickRepository: ClickRepository,
    private val matchRepository: MatchRepository,
    private val loginRepository: LoginRepository,
    private val preferenceProvider: PreferenceProvider
) : StompClient, WebSocketListener() {

    private var socket: WebSocket? = null
    private var outgoing = Channel<String>()
    private var missedChatMessageKeys: Queue<UUID> = ConcurrentLinkedQueue()

    private var webSocketEventChannel = Channel<WebSocketEvent>()
    override val webSocketEventFlow = webSocketEventChannel.consumeAsFlow()

    private val webSocketState = WebSocketState()


    init {
        applicationScope.launch {
            outgoing.consumeEach { socket?.send(it) }
        }

        chatRepository.sendChatMessageFlow.onEach { chatMessageDTO ->
            sendChatMessage(chatMessageDTO)
        }.launchIn(applicationScope)
    }

    override fun connect() {
        applicationScope.launch {
            if (webSocketState.isClosed()) {
                webSocketState.reset()
                chatRepository.clearChatMessages()
                missedChatMessageKeys.clear()
            }
            connectWebSocket(0L)
        }
    }

    private suspend fun connectWebSocket(connectionDelay: Long) {
        if (webSocketState.isConnectableAndSetToConnecting()) {
            delay(connectionDelay)
            if (webSocketState.isRefreshAccessToken()) {
                val response = loginRepository.refreshAccessToken()
                if (response.isSuccess()) {
                    println("response.isSuccess()")
                    webSocketState.setRefreshAccessToken(false)
                } else if (response.isError()) {
                    println("response.isError()")
                    webSocketState.setRefreshAccessToken(true)
                    if (ExceptionCode.isLoginException(response.error) || response.error == ExceptionCode.NO_INTERNET_CONNECTIVITY_EXCEPTION) {
                        disconnect(false, null)
                        sendErrorWebSocketEvent(response.error, response.errorMessage)
                    }
                    return
                }
            }

            val webSocketRequest = Request.Builder()
                .addHeader(HttpHeader.NO_AUTHENTICATION, true.toString())
                .url(EndPoint.WEB_SOCKET_ENDPOINT)
                .build()
            socket = okHttpClient.newWebSocket(webSocketRequest, this)
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        println("override fun onOpen(webSocket: WebSocket, response: Response)")
        applicationScope.launch {
            webSocketState.setSocketStatus(WebSocketStatus.OPEN)
            connectToStomp()
        }
    }

    private suspend fun connectToStomp() {
        println("private fun connectToStomp()")
        preferenceProvider.getAccessToken()?.let { accessToken ->
            val headers = mutableMapOf<String, String>()
            headers[StompHeader.VERSION] = SUPPORTED_VERSIONS
            headers[StompHeader.HEART_BEAT] = DEFAULT_HEART_BEAT
            headers[StompHeader.ACCEPT_LANGUAGE] = Locale.getDefault().toString()
            headers[HttpHeader.ACCESS_TOKEN] = accessToken
            socket?.send(StompFrame(StompFrame.Command.CONNECT, headers, null).compile())
        } ?: kotlin.run {
            disconnect(false, null)
            sendErrorWebSocketEvent(ExceptionCode.ACCESS_TOKEN_NOT_FOUND_EXCEPTION, null)
        }
    }

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

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {}

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        println("override fun onClosing(webSocket: WebSocket, code: Int, reason: String)")
        println("onClosing code: $code")
        socket?.close(1000, null)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        println("override fun onClosed(webSocket: WebSocket, code: Int, reason: String)")
        applicationScope.launch {
            webSocketState.setSocketStatus(WebSocketStatus.CLOSED)
            if (!webSocketState.isReconnect()) {
                clearMissedChatMessageKeys()
            }
            connectWebSocket(RECONNECT_DELAY)
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        println("override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?)")
        applicationScope.launch {
            webSocketState.setSocketStatus(WebSocketStatus.CLOSED)
            chatRepository.clearChatMessages()
            missedChatMessageKeys.clear()
            if (t is NoInternetConnectivityException) {
                webSocketState.setReconnect(false)
                sendErrorWebSocketEvent(ExceptionCode.getExceptionCodeFrom(t), null)
            }
            connectWebSocket(RECONNECT_DELAY)
        }
    }

    private suspend fun clearMissedChatMessageKeys() {
        chatRepository.clearChatMessages(missedChatMessageKeys.toList())
        missedChatMessageKeys.clear()
    }

    private suspend fun onConnectedFrameReceived() {
        println("private fun onConnectedFrameReceived()")
        webSocketState.setSocketStatus(WebSocketStatus.STOMP_CONNECTED)
        sendMissedMessages()
        subscribeToQueue()
    }

    private suspend fun onMessageFrameReceived(stompFrame: StompFrame) {
        when (stompFrame.getPushType()) {
            PushType.CHAT_MESSAGE -> {
                val chatMessageDTO = GsonProvider.gson.fromJson(stompFrame.payload, ChatMessageDTO::class.java)
                chatRepository.saveChatMessageReceived(chatMessageDTO)
            }
            PushType.CLICKED -> {
                val clickDTO = GsonProvider.gson.fromJson(stompFrame.payload, ClickDTO::class.java)
                clickRepository.saveClick(clickDTO)
            }
            PushType.MATCHED -> {
                val matchDTO = GsonProvider.gson.fromJson(stompFrame.payload, MatchDTO::class.java)
                matchRepository.saveMatch(matchDTO)
            }
        }
    }

    private suspend fun onReceiptFrameReceived(stompFrame: StompFrame) {
        stompFrame.payload?.let { payload ->
            val chatMessageReceiptDTO = GsonProvider.gson.fromJson(payload, ChatMessageReceiptDTO::class.java)
            chatRepository.saveChatMessageReceipt(chatMessageReceiptDTO)
        }
    }

    private suspend fun onErrorFrameReceived(stompFrame: StompFrame) {
        println("private fun onErrorFrameReceived(stompFrame: StompFrame): ${stompFrame.getError()} - ${stompFrame.getErrorMessage()}")
        stompFrame.getReceiptId()?.let { receiptId ->
            println("missedChatMessageKeys.add(receiptId): $receiptId")
            missedChatMessageKeys.add(receiptId)
        }

        when {
            stompFrame.getError() == ExceptionCode.EXPIRED_JWT_EXCEPTION -> {
                println("stompFrame.getError() == ExceptionCode.EXPIRED_JWT_TOKEN_EXCEPTION")
                webSocketState.setRefreshAccessToken(true)
            }
            ExceptionCode.isLoginException(stompFrame.getError()) -> {
                webSocketState.update(false, true, null, null)
                val webSocketEvent = WebSocketEvent.error(stompFrame.getError(), stompFrame.getErrorMessage())
                webSocketEventChannel.send(webSocketEvent)
            }
        }
    }

    private fun sendMissedMessages() {
        println("private fun sendMissedMessages()")
        while (missedChatMessageKeys.size > 0) {
            println("chatRepository.resendChatMessage(missedChatMessageKeys.poll()): ${missedChatMessageKeys.peek()}")
            applicationScope.launch {
                chatRepository.resendChatMessage(missedChatMessageKeys.poll())
            }
        }
    }

    private suspend fun sendChatMessage(chatMessageDTO: ChatMessageDTO) {
        println("private suspend fun sendChatMessage(chatMessageDTO: ChatMessageDTO)")

        if (webSocketState.isStompConnected()) {
            preferenceProvider.getAccessToken()?.let { accessToken ->
                val headers = mutableMapOf<String, String>()
                headers[StompHeader.DESTINATION] = EndPoint.STOMP_SEND_ENDPOINT
                headers[StompHeader.RECEIPT] = chatMessageDTO.id.toString()
                headers[StompHeader.ACCEPT_LANGUAGE] = Locale.getDefault().toString()
                headers[HttpHeader.ACCESS_TOKEN] = accessToken
                val stompFrame = StompFrame(StompFrame.Command.SEND, headers, GsonProvider.gson.toJson(chatMessageDTO))
                outgoing.send(stompFrame.compile())
            } ?: kotlin.run {
                disconnect(false, null)
                chatRepository.clearChatMessage(chatMessageDTO.id)
                sendErrorWebSocketEvent(ExceptionCode.ACCESS_TOKEN_NOT_FOUND_EXCEPTION, null)
            }
        } else {
            if (webSocketState.isDisconnected()) {
                chatRepository.clearChatMessage(chatMessageDTO.id)
            } else {
                missedChatMessageKeys.add(chatMessageDTO.id)
            }
        }
    }

    override fun disconnect() {
        println("override suspend fun disconnect()")
        applicationScope.launch {
            disconnect(null, true)
        }
    }

    private suspend fun disconnect(reconnect: Boolean?, disconnectedByUser: Boolean?) {
        webSocketState.update(reconnect, null, disconnectedByUser, null)
        socket?.close(1000, null)
    }

    private fun subscribeToQueue() {
        println("private fun subscribeToQueue()")
        val accountId = preferenceProvider.getAccountId()
        if (accountId == null) {
            sendErrorWebSocketEvent(ExceptionCode.ACCOUNT_ID_NOT_FOUND_EXCEPTION, null)
            return
        }

        val accessToken = preferenceProvider.getAccessToken()
        if (accessToken == null) {
            sendErrorWebSocketEvent(ExceptionCode.ACCESS_TOKEN_NOT_FOUND_EXCEPTION, null)
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

    private fun sendErrorWebSocketEvent(error: String?, errorMessage: String?) {
        val webSocketEvent = WebSocketEvent.error(error, errorMessage)
        applicationScope.launch {
            webSocketEventChannel.send(webSocketEvent)
        }
    }


    companion object {
        private const val SUPPORTED_VERSIONS = "1.1,1.2"
        private const val DEFAULT_HEART_BEAT = "0,0"
        private const val RECONNECT_DELAY = 10000L
        private const val QUEUE_PREFIX = "/queue/"
    }
}


// TODO: lifecycle event error and close socket in subscribe()
// TODO: what happens when exception is thrown in onFrame()