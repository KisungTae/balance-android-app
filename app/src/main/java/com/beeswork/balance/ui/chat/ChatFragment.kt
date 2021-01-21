package com.beeswork.balance.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
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
//            rvChat.smoothScrollToPosition(0)
            viewModel.sendChatMessage(etChatMessage.text.toString())

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

    private suspend fun setupMessageObserver() {
        val chatMessages = viewModel.chatMessages.await()
        chatMessages.observe(viewLifecycleOwner, Observer { pagedMessageList ->
            pagedMessageList?.let {
                chatPagedListAdapter.submitList(pagedMessageList)
            }
        })
    }

    private fun setupChatPagedList() {
        chatPagedListAdapter = ChatPagedListAdapter()

        chatPagedListAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    println("onItemRangeInserted")
//                    rvChat.smoothScrollToPosition(100)
//                    println("onItemRangeInserted item count: ${rvChat.adapter?.itemCount}")
                    rvChat.scrollToPosition(0)
                }
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