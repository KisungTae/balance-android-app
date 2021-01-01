package com.beeswork.balance.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.beeswork.balance.R
import com.beeswork.balance.internal.ChatIdNotFoundException
import com.beeswork.balance.internal.provider.PreferenceProvider
import com.beeswork.balance.ui.base.ScopeFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.factory
import org.kodein.di.generic.instance
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader


class ChatFragment: ScopeFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: ((Long) -> ChatViewModelFactory) by factory()
    private val preferenceProvider: PreferenceProvider by instance()
    private lateinit var viewModel: ChatViewModel
    private lateinit var chatPagedListAdapter: ChatPagedListAdapter
    private lateinit var stompClient: StompClient
    private lateinit var compositeDisposable: CompositeDisposable
    private var chatId: Long = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            ChatFragmentArgs.fromBundle(it)
        }?.let {
            chatId = it.chatId
            viewModel = ViewModelProvider(this, viewModelFactory(chatId)).get(ChatViewModel::class.java)
            bindUI()
        } ?: kotlin.run {
            println("argument is null")
        }
    }

    private fun bindUI() = launch {
        setupChatPagedList()
        setupMessageObserver()
        setupStompClient()
    }

    private fun resetSubscription() {
        compositeDisposable = CompositeDisposable()
    }

    private fun setupStompClient() {
        stompClient = Stomp.over(
            Stomp.ConnectionProvider.OKHTTP,
            "ws://10.0.2.2:8080/chat/websocket"
        )

        val headers = mutableListOf<StompHeader>()
        headers.add(StompHeader("LOGIN", "guest"))
        headers.add(StompHeader("PASSCODE", "guest"))

        stompClient.withClientHeartbeat(0).withServerHeartbeat(0)

        resetSubscription()

        val dispLifecycle: Disposable = stompClient.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { lifecycleEvent ->
                when (lifecycleEvent.type) {
                    LifecycleEvent.Type.OPENED -> println("Stomp connection opened")
                    LifecycleEvent.Type.ERROR -> println("Stomp connection error: ${lifecycleEvent.exception}")
                    LifecycleEvent.Type.CLOSED -> {
                        println("Stomp connection closed")
                        resetSubscription()
                    }
                    LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> println("Stomp failed server heartbeat")
                }
            }

        compositeDisposable.add(dispLifecycle)

        val queue = "/queue/${preferenceProvider.getAccountId()}-1"
//        stompClient.topic(queue).subscribe { topicMessage ->
//            println(topicMessage.getPayload())
//        }
        val dispTopic: Disposable = stompClient.topic(queue)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ topicMessage ->
                println("Received " + topicMessage.getPayload())
//                addItem(mGson.fromJson(topicMessage.getPayload(), EchoModel::class.java))
            }) { throwable -> println("Error on subscribe topic: $throwable") }

        compositeDisposable.add(dispTopic)

        stompClient.connect(headers)
    }

    private suspend fun setupMessageObserver() {
        val messages = viewModel.messages.await()
        messages.observe(viewLifecycleOwner, Observer { pagedMessageList ->
            pagedMessageList?.let {
                chatPagedListAdapter.submitList(pagedMessageList)
            }
        })
    }

    private fun setupChatPagedList() {
        chatPagedListAdapter = ChatPagedListAdapter()
        rvChat.adapter = chatPagedListAdapter

        val layoutManager = LinearLayoutManager(this@ChatFragment.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        layoutManager.reverseLayout = true
        rvChat.layoutManager = layoutManager
        rvChat.scrollToPosition(0)
    }

}