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
import java.util.concurrent.atomic.AtomicBoolean


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
    private var socketStatus = SocketStatus.CLOSED
    private var outgoing = Channel<String>()
    private var missedChatMessageKeys: Queue<Long> = ConcurrentLinkedQueue()

    private var refreshAccessToken = false
    private var disconnectedByUser = false
    private var reconnect = true

    private var webSocketEventChannel = Channel<WebSocketEvent>()
    override val webSocketEventFlow = webSocketEventChannel.consumeAsFlow()


    private var connectJob: Job? = null

    init {
        applicationScope.launch {
            outgoing.consumeEach { socket?.send(it) }
        }

        chatRepository.sendChatMessageFlow.onEach { chatMessageDTO ->
            sendChatMessage(chatMessageDTO)
        }.launchIn(applicationScope)
    }

    override suspend fun connect() {
        println("override suspend fun connect()")
        resetSocket()
        connectWebSocket(0L)
    }

    private fun resetSocket() {
        disconnectedByUser = false
        reconnect = true
        refreshAccessToken = false
        socket?.close(1000, null)
        socketStatus = SocketStatus.CLOSED
        connectJob?.cancel()
        clearMissedChatMessageKeys()
    }

    private fun connectWebSocket(connectionDelay: Long) {
        if (connectJob?.isActive != true && socketStatus == SocketStatus.CLOSED && reconnect && !disconnectedByUser) {
            socketStatus = SocketStatus.CONNECTING
            println("connectJob?.isCancelled ${connectJob?.isCancelled}")
            println("connectJob?.isCompleted ${connectJob?.isCompleted}")
            println("connectJob?.isActive ${connectJob?.isActive}")
            println("(connectJob?.isActive != true)")

            connectJob = applicationScope.launch {
                println("connectJob = applicationScope.launch")
                println("socketStatus: $socketStatus")
                println("disconnectedByUser: $disconnectedByUser")
                println("reconnect: $reconnect")
                delay(connectionDelay)
                openWebSocketConnection()
            }
            connectJob?.start()
        }
    }

    private suspend fun openWebSocketConnection() {
        println("socketStatus = SocketStatus.CONNECTING")
        if (refreshAccessToken) {
            val response = loginRepository.refreshAccessToken()
            if (response.isSuccess()) {
                println("response.isSuccess()")
                refreshAccessToken = false
            } else if (response.isError()) {
                println("response.isError()")
                refreshAccessToken = true
                if (ExceptionCode.isLoginException(response.error) || response.error == ExceptionCode.NO_INTERNET_CONNECTIVITY_EXCEPTION) {
                    clearMissedChatMessageKeys()
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

    override fun onOpen(webSocket: WebSocket, response: Response) {
        println("override fun onOpen(webSocket: WebSocket, response: Response)")
        socketStatus = SocketStatus.OPEN
        connectToStomp()
    }

    private fun connectToStomp() {
        println("private fun connectToStomp()")
        preferenceProvider.getAccessToken()?.let { accessToken ->
            applicationScope.launch {
                val headers = mutableMapOf<String, String>()
                headers[StompHeader.VERSION] = SUPPORTED_VERSIONS
                headers[StompHeader.HEART_BEAT] = DEFAULT_HEART_BEAT
                headers[StompHeader.ACCEPT_LANGUAGE] = Locale.getDefault().toString()
                headers[HttpHeader.ACCESS_TOKEN] = accessToken
                socket?.send(StompFrame(StompFrame.Command.CONNECT, headers, null).compile())
            }
        } ?: kotlin.run {
            sendErrorWebSocketEvent(ExceptionCode.ACCESS_TOKEN_NOT_FOUND_EXCEPTION, null)
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        val stompFrame = StompFrame.from(text)
        println("override fun onMessage(webSocket: WebSocket, text: String): ${stompFrame.command}")
        when (stompFrame.command) {
            StompFrame.Command.CONNECTED -> onConnectedFrameReceived()
            StompFrame.Command.MESSAGE -> onMessageFrameReceived(stompFrame)
            StompFrame.Command.RECEIPT -> onReceiptFrameReceived(stompFrame)
            StompFrame.Command.ERROR -> onErrorFrameReceived(stompFrame)
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {}

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        println("override fun onClosing(webSocket: WebSocket, code: Int, reason: String)")
        socket?.close(1000, null)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        println("override fun onClosed(webSocket: WebSocket, code: Int, reason: String)")
        socketStatus = SocketStatus.CLOSED
        if (!refreshAccessToken) {
            clearMissedChatMessageKeys()
        }
        connectWebSocket(RECONNECT_DELAY)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        println("override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?)")
        socketStatus = SocketStatus.CLOSED
        clearMissedChatMessageKeys()
        if (t is NoInternetConnectivityException) {
            sendErrorWebSocketEvent(ExceptionCode.getExceptionCodeFrom(t), null)
        } else {
            connectWebSocket(RECONNECT_DELAY)
        }
    }

    private fun clearMissedChatMessageKeys() {
        println("private fun clearMissedChatMessageKeys()")
        applicationScope.launch {
            chatRepository.updateChatMessageStatus(missedChatMessageKeys.toList(), ChatMessageStatus.ERROR)
            missedChatMessageKeys.clear()
        }
    }

    private fun onConnectedFrameReceived() {
        println("private fun onConnectedFrameReceived()")
        socketStatus = SocketStatus.STOMP_CONNECTED
        sendMissedMessages()
        subscribeToQueue()
    }

    private fun onMessageFrameReceived(stompFrame: StompFrame) {
        when (stompFrame.getPushType()) {
            PushType.CHAT_MESSAGE -> applicationScope.launch {
                val chatMessageDTO = GsonProvider.gson.fromJson(stompFrame.payload, ChatMessageDTO::class.java)
                chatRepository.saveChatMessageReceived(chatMessageDTO)
            }
            PushType.CLICKED -> applicationScope.launch {
                val clickDTO = GsonProvider.gson.fromJson(stompFrame.payload, ClickDTO::class.java)
                clickRepository.saveClick(clickDTO)
            }
            PushType.MATCHED -> applicationScope.launch {
                val matchDTO = GsonProvider.gson.fromJson(stompFrame.payload, MatchDTO::class.java)
                matchRepository.saveMatch(matchDTO)
            }
            else -> {
            }
        }
    }

    private fun onReceiptFrameReceived(stompFrame: StompFrame) {
        stompFrame.payload?.let {
            applicationScope.launch {
                val chatMessageReceiptDTO = GsonProvider.gson.fromJson(it, ChatMessageReceiptDTO::class.java)
                chatMessageReceiptDTO.key = stompFrame.getReceiptId()
                chatRepository.saveChatMessageReceipt(chatMessageReceiptDTO)
            }
        }
    }

    private fun onErrorFrameReceived(stompFrame: StompFrame) {
        println("private fun onErrorFrameReceived(stompFrame: StompFrame): ${stompFrame.getError()} - ${stompFrame.getErrorMessage()}")
        stompFrame.getReceiptId()?.let { receiptId ->
            println("missedChatMessageKeys.add(receiptId): $receiptId")
            missedChatMessageKeys.add(receiptId)
        }

        when {
            stompFrame.getError() == ExceptionCode.EXPIRED_JWT_EXCEPTION -> {
                println("stompFrame.getError() == ExceptionCode.EXPIRED_JWT_TOKEN_EXCEPTION")
                refreshAccessToken = true
            }
            ExceptionCode.isLoginException(stompFrame.getError()) -> {
                reconnect = false
                refreshAccessToken = true
                applicationScope.launch {
                    val webSocketEvent = WebSocketEvent.error(stompFrame.getError(), stompFrame.getErrorMessage())
                    webSocketEventChannel.send(webSocketEvent)
                }
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
        if (socketStatus == SocketStatus.STOMP_CONNECTED) {
            preferenceProvider.getAccessToken()?.let { accessToken ->
                applicationScope.launch {
                    val headers = mutableMapOf<String, String>()
                    headers[StompHeader.DESTINATION] = EndPoint.STOMP_SEND_ENDPOINT
                    headers[StompHeader.RECEIPT] = chatMessageDTO.key.toString()
                    headers[StompHeader.ACCEPT_LANGUAGE] = Locale.getDefault().toString()
                    headers[HttpHeader.ACCESS_TOKEN] = accessToken
                    val stompFrame = StompFrame(StompFrame.Command.SEND, headers, GsonProvider.gson.toJson(chatMessageDTO))
                    outgoing.send(stompFrame.compile())
                }
            } ?: kotlin.run {
                sendErrorWebSocketEvent(ExceptionCode.ACCESS_TOKEN_NOT_FOUND_EXCEPTION, null)
            }
        } else {
            missedChatMessageKeys.add(chatMessageDTO.key)
        }
    }

    override suspend fun disconnect() {
        println("override suspend fun disconnect()")
        disconnectedByUser = true
        socket?.close(1000, null)
        connectJob?.cancel()
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

    enum class SocketStatus {
        CONNECTING,
        OPEN,
        CLOSED,
        STOMP_CONNECTED
    }

}


// TODO: lifecycle event error and close socket in subscribe()
// TODO: what happens when exception is thrown in onFrame()