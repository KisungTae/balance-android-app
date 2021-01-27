package com.beeswork.balance.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.network.stomp.WebSocketLifeCycleEvent
import com.beeswork.balance.ui.base.ScopeFragment
import com.beeswork.balance.ui.dialog.ExceptionDialog
import com.beeswork.balance.ui.dialog.ExceptionDialogListener
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.factory


class ChatFragment : ScopeFragment(), KodeinAware, ExceptionDialogListener {

    override val kodein by closestKodein()
    private val viewModelFactory: ((ChatViewModelFactoryParameter) -> ChatViewModelFactory) by factory()
    private lateinit var viewModel: ChatViewModel
    private lateinit var chatPagingAdapter: ChatPagingAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var chatRecyclerViewAdapter: ChatRecyclerViewAdapter

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
        setupChatRecyclerView()
        setupWebSocketLifeCycleEventObserver()
        btnChatSend.setOnClickListener {
//            println("list size: ${chatPagingAdapter.currentList?.size}")
//            viewModel.sendChatMessage(etChatMessage.text.toString())
//            layoutManager.scrollToPositionWithOffset(0, 300)
            chatRecyclerViewAdapter.updateItem()
            if (etChatMessage.text.isNotEmpty()) {
                println("rvChat.scrollBy(0, scrollDy)")
                rvChat.scrollBy(0, scrollDy)
            }

        }
//        viewModel.chatMessages.await().observe(viewLifecycleOwner, {
//            chatPagingAdapter.submitList(it)
//        })
//        viewModel.connectChat()
    }

    var scrollDy = 0
    var scrolling = false

    private fun setupChatRecyclerView() {
//        chatPagingAdapter = ChatPagingAdapter()
        chatRecyclerViewAdapter = ChatRecyclerViewAdapter()
        rvChat.adapter = chatRecyclerViewAdapter
        layoutManager = LinearLayoutManager(this@ChatFragment.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        layoutManager.reverseLayout = true
        rvChat.layoutManager = layoutManager
        CoroutineScope(Dispatchers.IO).launch {
            val list = viewModel.getMessages()
            withContext(Dispatchers.Main) {
                chatRecyclerViewAdapter.submit(list)
            }

        }

        rvChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                println("dx: $dx - dy: $dy")
                scrollDy = dy
                super.onScrolled(recyclerView, dx, dy)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        scrolling = false
                    }
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        scrolling = true
                    }

                }
            }
        })

//        lifecycleScope.launch {
//            viewModel.chatMessages.collectLatest {
//                chatPagingAdapter.submitData(it)
//            }
//        }

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