package com.beeswork.balance.ui.chat

import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.internal.constant.BalanceURL
import com.beeswork.balance.internal.lazyDeferred
import com.beeswork.balance.internal.provider.PreferenceProvider
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription



class ChatViewModel(
    private val chatId: Long,
    private val balanceRepository: BalanceRepository,
    private val preferenceProvider: PreferenceProvider
) : ViewModel() {

    private val pagedListConfig = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setMaxSize(CHAT_MAX_PAGE_SIZE)
        .setInitialLoadSizeHint(CHAT_PAGE_SIZE)
        .setPageSize(CHAT_PAGE_SIZE)
        .setPrefetchDistance(CHAT_PAGE_PREFETCH_DISTANCE)
        .build()

    val messages by lazyDeferred {
        LivePagedListBuilder(balanceRepository.getMessages(chatId), pagedListConfig).build()
    }

//    private lateinit var stompClient: StompClient
//    private lateinit var compositeDisposables: CompositeDisposable

    init {
//        setupStompClient()
    }

//    private fun setupStompClient() {
//        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, BalanceURL.WEB_SOCKET_ENDPOINT)
//        compositeDisposables = CompositeDisposable()
//        compositeDisposables.add(setupStompClientLifeCycle())
//        setupSubscription()
//        stompClient.connect()
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
//        compositeDisposables.add(stompClient.topic(queueName(), subscriptionHeaders())
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
//        return stompClient.lifecycle()
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
//
//    private fun queueName(): String {
//        return "/queue/${preferenceProvider.getAccountId()}-$chatId"
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        compositeDisposables.clear()
//    }

    companion object {
        const val CHAT_PAGE_SIZE = 30
        const val CHAT_PAGE_PREFETCH_DISTANCE = CHAT_PAGE_SIZE * 2
        const val CHAT_MAX_PAGE_SIZE = CHAT_PAGE_PREFETCH_DISTANCE * 2 + CHAT_PAGE_SIZE
    }
}