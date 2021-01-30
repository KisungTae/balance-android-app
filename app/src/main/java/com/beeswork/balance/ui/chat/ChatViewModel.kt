package com.beeswork.balance.ui.chat

import androidx.lifecycle.ViewModel
import androidx.paging.*
import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.data.network.stomp.StompClient
import com.beeswork.balance.internal.util.lazyDeferred


class ChatViewModel(
    private val chatId: Long,
    private val matchedId: String,
    private val balanceRepository: BalanceRepository,
    private val stompClient: StompClient
) : ViewModel() {

    private val pagedListConfig = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setMaxSize(CHAT_MAX_PAGE_SIZE)
        .setInitialLoadSizeHint(CHAT_PAGE_SIZE)
        .setPageSize(CHAT_PAGE_SIZE)
//        .setPrefetchDistance(CHAT_PAGE_PREFETCH_DISTANCE)
        .build()



    val chatMessages by lazyDeferred {
        LivePagedListBuilder(balanceRepository.getChatMessages(chatId), pagedListConfig).build()
    }

//    val chatMessages = Pager(
//        PagingConfig(
//            pageSize = 30,
//            enablePlaceholders = true,
//            maxSize = 150
//        )
//    ) {
//        balanceRepository.getChatMessages(chatId)
//    }.flow.cachedIn(viewModelScope)

    val webSocketLifeCycleEvent = stompClient.webSocketEvent

    fun getMessages(): List<ChatMessage> {
        return balanceRepository.getMessages(chatId)
    }

    fun connectChat() {
        stompClient.connectChat(chatId, matchedId)
    }

    fun sendChatMessage(body: String) {
        stompClient.send(chatId, matchedId, body)
    }

    fun disconnectChat() {
        stompClient.disconnectChat()
    }




//    private lateinit var stompClient2: ua.naiksoftware.stomp.StompClient
//    private lateinit var compositeDisposables: CompositeDisposable
//
//    init {
//
//        setupStompClient()
//    }
//
//    private fun setupStompClient() {
//        stompClient2 = Stomp.over(Stomp.ConnectionProvider.OKHTTP, BalanceURL.WEB_SOCKET_ENDPOINT)
//        compositeDisposables = CompositeDisposable()
//        compositeDisposables.add(setupStompClientLifeCycle())
//        setupSubscription()
//        stompClient2.connect()
//    }
//
//    private fun getStompConnectionHeaders(): MutableList<StompHeader> {
//        val headers = mutableListOf<StompHeader>()
//        headers.add(StompHeader("LOGIN", "guest"))
//        headers.add(StompHeader("PASSCODE", "guest"))
//        return headers
//    }
//
//    private fun setupSubscription() {
//        compositeDisposables.add(stompClient2.topic("/queue/test-1", subscriptionHeaders())
//            .doOnError {
//                println("setupSubscription doOnError: ${it.message}")
//            }
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ topicMessage ->
//                println("stompCommand: " + topicMessage.stompCommand)
//                println("Received " + topicMessage.payload)
//            }) { throwable -> println("Error on subscribe topic: $throwable") })
//    }
//
//    private fun subscriptionHeaders(): MutableList<StompHeader> {
//        val headers = mutableListOf<StompHeader>()
//        headers.add(StompHeader("accountId", preferenceProvider.getAccountId()))
//        headers.add(StompHeader("identityToken", preferenceProvider.getIdentityToken()))
//        headers.add(StompHeader("chatId", chatId.toString()))
//        headers.add(StompHeader("accept-language", "kr"))
//        return headers
//    }
//
//    private fun setupStompClientLifeCycle(): Disposable {
//        return stompClient2.lifecycle()
//            .doOnError {
//                println("setupStompClientLifeCycle doOnError")
//            }
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ lifecycleEvent ->
//                when (lifecycleEvent.type) {
//                    LifecycleEvent.Type.OPENED -> println("Stomp connection opened")
//                    LifecycleEvent.Type.ERROR -> println("Stomp connection error: ${lifecycleEvent.exception}")
//                    LifecycleEvent.Type.CLOSED -> {
//                        println("Stomp connection closed!!!!!!123")
//                        compositeDisposables.clear()
//                    }
//                    LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> println("Stomp failed server heartbeat")
//                    else -> println("dddd")
//                }
//            }) { throwable -> println("lifecycle error!!!!!!!!!: $throwable") }
//    }



//    override fun onCleared() {
//        super.onCleared()
//        compositeDisposables.clear()
//    }





    companion object {
        const val CHAT_PAGE_SIZE = 50
        const val CHAT_PAGE_PREFETCH_DISTANCE = CHAT_PAGE_SIZE
        const val CHAT_MAX_PAGE_SIZE = CHAT_PAGE_SIZE + (CHAT_PAGE_PREFETCH_DISTANCE * 2)
    }
}


