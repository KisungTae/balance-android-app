package com.beeswork.balance.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentChatBinding
import com.beeswork.balance.service.stomp.WebSocketEvent
import com.beeswork.balance.ui.common.ScopeFragment
import com.beeswork.balance.ui.dialog.ExceptionDialog
import com.beeswork.balance.ui.dialog.ExceptionDialogListener
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.factory
import java.sql.SQLOutput


class ChatFragment : ScopeFragment(), KodeinAware, ExceptionDialogListener {

    override val kodein by closestKodein()
    private val viewModelFactory: ((Long) -> ChatViewModelFactory) by factory()
    private lateinit var viewModel: ChatViewModel
    private lateinit var chatPagingAdapter: ChatPagingAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var chatRecyclerViewAdapter: ChatRecyclerViewAdapter
    private lateinit var binding: FragmentChatBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        println("chat onCreateView()")
        binding = FragmentChatBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        println("chat onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            viewModelFactory(1)
        ).get(ChatViewModel::class.java)
//        arguments?.let {
//            ChatFragmentArgs.fromBundle(it)
//        }?.let {
//            viewModel = ViewModelProvider(
//                this,
//                viewModelFactory(it.chatId)
//            ).get(ChatViewModel::class.java)
//            bindUI()
//        } ?: kotlin.run {
//            ExceptionDialog(getString(R.string.chat_id_not_found_exception), this).show(
//                childFragmentManager,
//                ExceptionDialog.TAG
//            )
//        }
    }

    private fun bindUI() {

//        setupChatRecyclerView()
//        setupWebSocketLifeCycleEventObserver()
//
//        binding.btnChatSend.setOnClickListener {
//            viewModel.sendChatMessage(binding.etChatMessageBody.text.toString())
//        }
//        observeChatMessageEvent()
//        viewModel.fetchInitialChatMessages()

//        viewModel.connectChat()
    }

    private fun closeChat() {

    }

    fun setup(chatId: Long) {
        println("setup(chatId: Long): $chatId")
    }

    private fun observeChatMessageEvent() {
        viewModel.chatMessageEvent.observe(viewLifecycleOwner, {
            when (it.type) {
                ChatMessageEvent.Type.FETCH_ERROR -> {
                    ExceptionDialog(it.errorMessage, this).show(
                        childFragmentManager,
                        ExceptionDialog.TAG
                    )
//                    binding.llChatLoading.visibility = View.GONE
                }
                ChatMessageEvent.Type.FETCH -> {

                }
            }
        })
    }



    private fun setupChatRecyclerView() {
        chatRecyclerViewAdapter = ChatRecyclerViewAdapter()
        binding.rvChat.adapter = chatRecyclerViewAdapter
        layoutManager = LinearLayoutManager(this@ChatFragment.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        layoutManager.reverseLayout = true
        binding.rvChat.layoutManager = layoutManager

    }



    private fun setupWebSocketLifeCycleEventObserver() {
        viewModel.webSocketLifeCycleEvent.observe(viewLifecycleOwner, {
            when (it.type) {
                WebSocketEvent.Type.ERROR -> {
                    ExceptionDialog(it.errorMessage, null).show(
                        childFragmentManager,
                        ExceptionDialog.TAG
                    )
                }
            }
        })
    }

    override fun onClickExceptionDialogCloseBtn() {
//        Navigation.findNavController(requireView())
//            .navigate(R.id.action_chatFragment_to_matchFragment)
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