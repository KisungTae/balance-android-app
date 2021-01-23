package com.beeswork.balance.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.paging.PagedListAdapter
import androidx.paging.PositionalDataSource
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.network.stomp.WebSocketLifeCycleEvent
import com.beeswork.balance.ui.base.ScopeFragment
import com.beeswork.balance.ui.dialog.ExceptionDialog
import com.beeswork.balance.ui.dialog.ExceptionDialogListener
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.factory


class ChatFragment : ScopeFragment(), KodeinAware, ExceptionDialogListener {

    override val kodein by closestKodein()
    private val viewModelFactory: ((ChatViewModelFactoryParameter) -> ChatViewModelFactory) by factory()
    private lateinit var viewModel: ChatViewModel
    private lateinit var chatPagedListAdapter: ChatPagedListAdapter

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
            viewModel = ViewModelProvider(
                this,
                viewModelFactory(ChatViewModelFactoryParameter(it.chatId, it.matchedId))
            ).get(ChatViewModel::class.java)
            bindUI()
        } ?: kotlin.run {
            ExceptionDialog(getString(R.string.chat_id_not_found_exception), this).show(
                childFragmentManager,
                ExceptionDialog.TAG
            )
        }
    }

    private fun bindUI() = launch {
        setupChatPagedList()
        setupMessageObserver()
        setupWebSocketLifeCycleEventObserver()
        btnChatSend.setOnClickListener {
//            println("item count: ${rvChat.adapter?.itemCount}")
//            val text = etChatMessage.text
//            println("text: $text")
//            if (!text.isEmpty()) {
//                println("rvChat.smoothScrollToPosition(0)")
//
//
//            } else {
//                println("rvChat.scrollToPosition(0)")
//                rvChat.scrollToPosition(0)
//            }
//
//            rvChat.smoothScrollToPosition(0)
//            println("before viewModel.sendChatMessage(etChatMessage.text.toString())")
            viewModel.sendChatMessage(etChatMessage.text.toString())
//            chatPagedListAdapter.currentList?.loadAround(0)

//            val offset = chatPagedListAdapter.currentList?.positionOffset
//            offset?.let {
//                if (it == 0) rvChat.scrollToPosition(0)
//                else chatPagedListAdapter.currentList?.loadAround(0)
//            }

//            chatPagedListAdapter.currentList?.loadAround(50)
//            val d = chatPagedListAdapter.currentList?.dataSource as PositionalDataSource

//            chatPagedListAdapter.currentList?.positionOffset?.let {
//                if (it == 0) rvChat.scrollToPosition(0)
//                else chatPagedListAdapter.currentList?.loadAround(0)
//            }



//            val layoutManager = rvChat.layoutManager as LinearLayoutManager
//            println("layoutManager.findFirstCompletelyVisibleItemPosition(): ${layoutManager.findFirstCompletelyVisibleItemPosition()}")
//            println("layoutManager.findFirstVisibleItemPosition(): ${layoutManager.findFirstVisibleItemPosition()}")
//            println("layoutManager.findLastCompletelyVisibleItemPosition(): ${layoutManager.findLastCompletelyVisibleItemPosition()}")
//            println("layoutManager.findLastVisibleItemPosition(): ${layoutManager.findLastVisibleItemPosition()}")
//
//            println("chatPagedListAdapter.currentList?.positionOffset: ${chatPagedListAdapter.currentList?.positionOffset}")
//            println("chatPagedListAdapter.currentList?.lastKey: ${chatPagedListAdapter.currentList?.lastKey}")

//            chatPagedListAdapter.currentList?.loadAround(0)



        }
//        viewModel.connectChat()
    }

    private fun setupWebSocketLifeCycleEventObserver() {
        viewModel.webSocketLifeCycleEvent.observe(viewLifecycleOwner, {
            when (it.type) {
                WebSocketLifeCycleEvent.Type.ERROR -> {
                    ExceptionDialog(it.errorMessage, null).show(
                        childFragmentManager,
                        ExceptionDialog.TAG
                    )
                }
            }
        })
    }

    var init = true

    private suspend fun setupMessageObserver() {
        val chatMessages = viewModel.chatMessages.await()
        chatMessages.observe(viewLifecycleOwner, Observer { pagedMessageList ->
            pagedMessageList?.let {
                val list = chatPagedListAdapter.currentList

                chatPagedListAdapter.submitList(pagedMessageList)
            }
        })

        rvChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
//                println("onScrolled")
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    println("newState == RecyclerView.SCROLL_STATE_IDLE")
                }
            }
        })

    }

    private fun setupChatPagedList() {
        chatPagedListAdapter = ChatPagedListAdapter()


        chatPagedListAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
//                println("==================onItemRangeInserted START================")
//                println("item count inserted: $itemCount")
//                println("list size: ${rvChat.adapter?.itemCount}")
//                println("positionStart: $positionStart")
            }
        })

        rvChat.adapter = chatPagedListAdapter

        val layoutManager = LinearLayoutManager(this@ChatFragment.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        layoutManager.reverseLayout = true
        rvChat.layoutManager = layoutManager

    }

    override fun onClickExceptionDialogCloseBtn() {
        Navigation.findNavController(requireView())
            .navigate(R.id.action_chatFragment_to_matchFragment)
    }

    override fun onResume() {
        super.onResume()
        viewModel.connectChat()
    }

    override fun onPause() {
        super.onPause()
        viewModel.disconnectChat()
    }


}