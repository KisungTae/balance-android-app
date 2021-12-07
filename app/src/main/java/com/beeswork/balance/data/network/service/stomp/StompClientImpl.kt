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
import com.beeswork.balance.internal.constant.EndPoint
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.constant.PushType
import com.beeswork.balance.internal.constant.StompHeader
import com.beeswork.balance.internal.exception.NoInternetConnectivityException
import com.beeswork.balance.internal.provider.gson.GsonProvider
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.*
import okio.ByteString
import java.net.ConnectException
import java.net.SocketTimeoutException
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
    private var socketStatus = SocketStatus.CLOSED
    private var outgoing = Channel<String>()
    private var missedMessageKeys: Queue<Long> = ConcurrentLinkedQueue()

    private var refreshAccessToken = false
    private var disconnectedByUser = false
    private var reconnect = false

    private var webSocketEventChannel = Channel<WebSocketEvent>()
    override val webSocketEventFlow = webSocketEventChannel.consumeAsFlow()

    init {
        applicationScope.launch {
            outgoing.consumeEach { socket?.send(it) }
        }

        chatRepository.sendChatMessageFlow.onEach { chatMessageDTO ->
            sendChatMessage(chatMessageDTO)
        }.launchIn(applicationScope)
    }

    override suspend fun connect() {
        disconnectedByUser = false
        if (socketStatus == SocketStatus.CLOSED) {
            socketStatus = SocketStatus.CONNECTING
            val webSocketRequest = Request.Builder()
                .addHeader(HttpHeader.NO_AUTHENTICATION, true.toString())
                .url(EndPoint.WEB_SOCKET_ENDPOINT)
                .build()
            socket = okHttpClient.newWebSocket(webSocketRequest, this)
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        socketStatus = SocketStatus.OPEN
        connectToStomp()
    }

    private fun connectToStomp() {
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
        when (stompFrame.command) {
            StompFrame.Command.CONNECTED -> onConnectedFrameReceived()
            StompFrame.Command.MESSAGE -> onMessageFrameReceived(stompFrame)
            StompFrame.Command.RECEIPT -> onReceiptFrameReceived(stompFrame)
            StompFrame.Command.ERROR -> onErrorFrameReceived(stompFrame)
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {}

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        println("override fun onClosing(webSocket: WebSocket, code: Int, reason: String) ${reason}")
        socketStatus = SocketStatus.CLOSED
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
//      TODO: clear chatMessageKeys and update the status in database
        println("override fun onClosed reason: ${reason}")
        socketStatus = SocketStatus.CLOSED
        scheduleConnect()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        socketStatus = SocketStatus.CLOSED
        sendErrorWebSocketEvent(ExceptionCode.getExceptionCodeFrom(t), null)
        if (t !is NoInternetConnectivityException) {
            scheduleConnect()
        }
    }

    private fun onConnectedFrameReceived() {
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
        val webSocketEvent = WebSocketEvent.error(stompFrame.getError(), stompFrame.getErrorMessage())
        refreshAccessToken = stompFrame.getError() == ExceptionCode.EXPIRED_JWT_EXCEPTION
        reconnect = !ExceptionCode.isLoginException(stompFrame.getError())

        stompFrame.getReceiptId()?.let { receiptId ->
            missedMessageKeys.add(receiptId)
        }

        applicationScope.launch {
            webSocketEventChannel.send(webSocketEvent)
            socket?.close(1000, null)
        }
    }


    private fun sendMissedMessages() {
        for (i in 1..10L) {
            missedMessageKeys.add(i)
        }

        while (missedMessageKeys.size > 0) {
            applicationScope.launch {
                chatRepository.resendChatMessage(missedMessageKeys.poll())
            }
        }
    }


    private suspend fun sendChatMessage(chatMessageDTO: ChatMessageDTO) {
        if (socketStatus == SocketStatus.STOMP_CONNECTED) {
            applicationScope.launch {
                val headers = mutableMapOf<String, String>()
                headers[StompHeader.DESTINATION] = EndPoint.STOMP_SEND_ENDPOINT
                headers[StompHeader.RECEIPT] = chatMessageDTO.key.toString()
                headers[StompHeader.ACCEPT_LANGUAGE] = Locale.getDefault().toString()
                headers[HttpHeader.ACCESS_TOKEN] = "${preferenceProvider.getAccessToken()}"
                val stompFrame = StompFrame(StompFrame.Command.SEND, headers, GsonProvider.gson.toJson(chatMessageDTO))
                outgoing.send(stompFrame.compile())
            }
        } else {
            missedMessageKeys.add(chatMessageDTO.key)
        }

//        if (socketStatus == SocketStatus.CLOSED) {
//            socket = null
//            connect()
//        }
//        if (socketStatus == SocketStatus.CONNECTING) delay(CONNECTING_DELAY)
//
//        if (socketStatus == SocketStatus.OPEN) {
//            val headers = mutableMapOf<String, String>()
//            headers[StompHeader.DESTINATION] = EndPoint.STOMP_SEND_ENDPOINT
//            headers[StompHeader.RECEIPT] = chatMessageDTO.key.toString()
//            headers[StompHeader.ACCEPT_LANGUAGE] = Locale.getDefault().toString()
//            headers[HttpHeader.ACCESS_TOKEN] = "${preferenceProvider.getAccessToken()}"
//            val stompFrame = StompFrame(StompFrame.Command.SEND, headers, GsonProvider.gson.toJson(chatMessageDTO))
//            outgoing.send(stompFrame.compile())
//        } else {
//            chatRepository.saveChatMessageReceipt(ChatMessageReceiptDTO(chatMessageDTO.key))
//        }
    }

    override suspend fun disconnect() {
        disconnectedByUser = true
        socket?.close(1000, null)
    }


    private fun subscribeToQueue() {
        preferenceProvider.getAccountId()?.let { accountId ->
            applicationScope.launch {
                val headers = mutableMapOf<String, String>()
                headers[StompHeader.DESTINATION] = getDestination(preferenceProvider.getAccountId())
                headers[StompHeader.ACCEPT_LANGUAGE] = Locale.getDefault().toString()
                headers[HttpHeader.ACCESS_TOKEN] = "${preferenceProvider.getAccessToken()}"
                socket?.send(StompFrame(StompFrame.Command.SUBSCRIBE, headers, null).compile())
            }
        } ?: kotlin.run {
            sendErrorWebSocketEvent(ExceptionCode.ACCOUNT_ID_NOT_FOUND_EXCEPTION, null)
        }
    }

    private fun sendErrorWebSocketEvent(error: String?, errorMessage: String?) {
        val webSocketEvent = WebSocketEvent.error(error, errorMessage)
        applicationScope.launch {
            webSocketEventChannel.send(webSocketEvent)
        }
    }

    private fun getDestination(id: UUID?): String {
        return "/queue/${id?.toString()}"
    }

    private fun scheduleConnect() {
        if (reconnect && !disconnectedByUser) {
            applicationScope.launch {
                delay(SCHEDULE_CONNECT_DELAY)
                if (refreshAccessToken) {
                    val response = loginRepository.refreshAccessToken()
                    if (response.isError()) {
                        webSocketEventChannel.send(WebSocketEvent.error(response.error, response.errorMessage))
                        return@launch
                    }
                }
                connect()
            }
        }
    }


    companion object {
        private const val SUPPORTED_VERSIONS = "1.1,1.2"
        private const val DEFAULT_HEART_BEAT = "0,0"
        private const val SCHEDULE_CONNECT_DELAY = 10000000L
    }

    enum class SocketStatus {
        CONNECTING,
        OPEN,
        CLOSED,
        STOMP_CONNECTED
    }

}


// TODO: when acceess token is null, then throw eception and implement catche xception in viewmodel
// TODO: change "${preferenceProvider.getAccessToken()}" to if acess ntoken then throw exception

// TODO: lifecycle event error and close socket in subscribe()
// TODO: what happens when exception is thrown in onFrame()