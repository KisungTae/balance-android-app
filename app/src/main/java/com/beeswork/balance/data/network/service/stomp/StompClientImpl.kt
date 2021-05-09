package com.beeswork.balance.data.network.service.stomp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.data.network.response.swipe.ClickDTO
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
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import okhttp3.*
import okio.ByteString
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.*


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
    private var socketStatus = SocketStatus.CLOSED

    private var chatMessageReceiptChannel = Channel<ChatMessageDTO>()
    override val chatMessageReceiptFlow = chatMessageReceiptChannel.consumeAsFlow()

    private var chatMessageChannel = Channel<ChatMessageDTO>()
    override val chatMessageReceivedFlow = chatMessageChannel.consumeAsFlow()

    private var matchedChannel = Channel<MatchDTO>()
    override val matchedFlow = matchedChannel.consumeAsFlow()

    private var clickedChannel = Channel<ClickDTO>()
    override val clickedFlow = clickedChannel.consumeAsFlow()

    private val _webSocketEventLiveData = MutableLiveData<WebSocketEvent>()
    override val webSocketEventLiveData: LiveData<WebSocketEvent> get() = _webSocketEventLiveData

    init {
        scope.launch {
            outgoing.consumeEach { socket?.send(it) }
        }
    }


    override fun onOpen(webSocket: WebSocket, response: Response) {
        socketStatus = SocketStatus.OPEN
        connectToStomp()
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        val stompFrame = StompFrame.from(text)
        when (stompFrame.command) {
            StompFrame.Command.CONNECTED -> subscribeToQueue()
            StompFrame.Command.MESSAGE -> onMessageFrameReceived(stompFrame)
            StompFrame.Command.RECEIPT -> onReceiptFrameReceived(stompFrame)
            StompFrame.Command.ERROR -> onErrorFrameReceived(stompFrame)
            else -> println("stompframe.command when else here")
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {}

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        println("onClosing!!!!!!!!!")
        socketStatus = SocketStatus.CLOSED
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        println("onClosed!!!!!!!!!!!")
        socketStatus = SocketStatus.CLOSED
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        socketStatus = SocketStatus.CLOSED
        val error: String? = when (t) {
            is SocketTimeoutException -> ExceptionCode.SOCKET_TIMEOUT_EXCEPTION
            is NoInternetConnectivityException -> ExceptionCode.NO_INTERNET_CONNECTIVITY_EXCEPTION
            is ConnectException -> ExceptionCode.CONNECT_EXCEPTION
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
                chatMessageChannel.send(GsonProvider.gson.fromJson(stompFrame.payload, ChatMessageDTO::class.java))
            }
            PushType.CLICKED -> scope.launch {
                clickedChannel.send(GsonProvider.gson.fromJson(stompFrame.payload, ClickDTO::class.java))
            }
            PushType.MATCHED -> scope.launch {
                matchedChannel.send(GsonProvider.gson.fromJson(stompFrame.payload, MatchDTO::class.java))
            }
            else -> { }
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

    override fun sendChatMessage(key: Long, chatId: Long, swipedId: UUID, body: String) {
        scope.launch {
            if (socketStatus == SocketStatus.CLOSED) connect()
            while (socketStatus == SocketStatus.CONNECTING) {}

            if (socketStatus == SocketStatus.OPEN) {
                val headers = mutableMapOf<String, String>()
                headers[StompHeader.DESTINATION] = EndPoint.STOMP_SEND_ENDPOINT
                headers[StompHeader.IDENTITY_TOKEN] = "${preferenceProvider.getIdentityToken()?.toString()}"
                headers[StompHeader.RECEIPT] = key.toString()
                headers[StompHeader.ACCEPT_LANGUAGE] = Locale.getDefault().toString()
                val chatMessageDTO = ChatMessageDTO(chatId, body, preferenceProvider.getAccountId(), swipedId)
                val stompFrame = StompFrame(StompFrame.Command.SEND, headers, GsonProvider.gson.toJson(chatMessageDTO))
                outgoing.send(stompFrame.compile())
            } else if (socketStatus == SocketStatus.CLOSED) {
                chatMessageReceiptChannel.send(ChatMessageDTO(key, chatId))
            }
        }
    }

    override fun disconnect() {
        socket?.close(1000, null)
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
            headers[StompHeader.IDENTITY_TOKEN] = "${preferenceProvider.getIdentityToken()?.toString()}"
            socket?.send(StompFrame(StompFrame.Command.SUBSCRIBE, headers, null).compile())
        }
    }

    private fun getDestination(id: UUID?): String {
        return "/queue/${id?.toString()}"
    }

    companion object {
        private const val SUPPORTED_VERSIONS = "1.1,1.2"
        private const val DEFAULT_HEART_BEAT = "0,0"
    }

}


// TODO: change message in stompframe to message entity
// TODO: lifecycle event error and close socket in subscribe()
// TODO: what happens when exception is thrown in onFrame()

// TODO: send chatmessage and then if successful, then check active of chat set it to true
// TODO: when send if not connected or web scoket closed, then push the message to buffer and when connected then clear buffer
// TODO: init outoing launch in connect() or init()