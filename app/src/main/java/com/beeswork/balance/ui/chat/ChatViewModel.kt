package com.beeswork.balance.ui.chat

import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.data.network.stomp.StompClient
import com.beeswork.balance.data.network.stomp.StompFrame
import com.beeswork.balance.data.network.stomp.WebSocketLifeCycleEvent
import com.beeswork.balance.internal.constant.BalanceURL
import com.beeswork.balance.internal.lazyDeferred
import com.beeswork.balance.internal.provider.PreferenceProvider
import com.neovisionaries.ws.client.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import ua.naiksoftware.stomp.Stomp

import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader
import java.util.*


class ChatViewModel(
    private val chatId: Long,
    private val balanceRepository: BalanceRepository,
    private val preferenceProvider: PreferenceProvider,
    private val stompClient: StompClient
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

    val webSocketLifeCycleEvent = stompClient.webSocketLifeCycleEvent
    val stompFrame = stompClient.stompFrame

    private fun queueName(): String {
        return "/queue/${preferenceProvider.getAccountId()}-$chatId"
    }

    // TODO: remove id parameter
    fun subscribe(id: String) {
        stompClient.subscribe("/queue/$id-$chatId")
    }

    // TODO: remove matchedId
    fun send(matchedId: String, message: String) {
        stompClient.send(matchedId, chatId, message)
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
        const val CHAT_PAGE_SIZE = 30
        const val CHAT_PAGE_PREFETCH_DISTANCE = CHAT_PAGE_SIZE * 2
        const val CHAT_MAX_PAGE_SIZE = CHAT_PAGE_PREFETCH_DISTANCE * 2 + CHAT_PAGE_SIZE
    }
}


