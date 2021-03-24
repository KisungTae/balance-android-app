package com.beeswork.balance.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentChatBinding
import com.beeswork.balance.internal.constant.BundleKey
import com.beeswork.balance.ui.common.ScopeFragment
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.dialog.ExceptionDialogListener
import com.beeswork.balance.ui.mainviewpager.MainViewPagerFragment
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.factory


class ChatFragment : ScopeFragment(), KodeinAware, ExceptionDialogListener {

    override val kodein by closestKodein()

    private val viewModelFactory: ((Long) -> ChatViewModelFactory) by factory()
    private var viewModel: ChatViewModel? = null

    //    private lateinit var chatPagingAdapter: ChatPagingAdapter
//    private lateinit var layoutManager: LinearLayoutManager
//    private lateinit var chatRecyclerViewAdapter: ChatRecyclerViewAdapter
    private lateinit var binding: FragmentChatBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { bundle ->
            bundle.getString(BundleKey.CHAT_ID)?.toLongOrNull()
        }?.let { chatId ->
            viewModel = ViewModelProvider(this, viewModelFactory(chatId)).get(ChatViewModel::class.java)
            bindUI()
        } ?: kotlin.run {
            ErrorDialog(null, getString(R.string.chat_id_not_found_exception), null).show(
                childFragmentManager,
                ErrorDialog.TAG
            )
        }
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
        setupBackPressedDispatcherCallback()
    }

    private fun setupBackPressedDispatcherCallback() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                popBackToMatch()
            }
        })
    }


    private fun setupToolBar() {
        binding.tbChat.inflateMenu(R.menu.chat_tool_bar)
        binding.tbChat.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.miChatLeave -> {
                    true
                }
                R.id.miChatReport -> {
                    true
                }
                R.id.miChatSearch -> {
                    showSearchToolBar()
                    true
                }
                else -> false
            }
        }
        binding.btnChatSearchClose.setOnClickListener { hideSearchToolBar() }
        binding.btnChatBack.setOnClickListener { popBackToMatch() }
    }

    private fun getChatIdFromBundle() {

    }

    private fun popBackToMatch() {
        requireActivity().supportFragmentManager.popBackStack(MainViewPagerFragment.TAG, POP_BACK_STACK_INCLUSIVE)
    }

    private fun hideSearchToolBar() {
        binding.tbChatSearch.visibility = View.GONE
        binding.tbChat.visibility = View.VISIBLE
//        viewModel.changeChatSearchKeyword("")
    }

    private fun showSearchToolBar() {
        binding.tbChat.visibility = View.GONE
        binding.tbChatSearch.visibility = View.VISIBLE
    }


    private fun closeChat() {

    }

    fun setup(chatId: Long) {

        println("chat setup: $chatId")

//        val viewModelFactory: ((Long) -> ChatViewModelFactory) by factory()
//        viewModel = ViewModelProvider(
//            this,
//            viewModelFactory(chatId)
//        ).get(ChatViewModel::class.java)

    }

    fun reset() {
        println("chat reset")
//        viewModel = null
    }

    private fun observeChatMessageEvent() {
//        viewModel.chatMessageEvent.observe(viewLifecycleOwner, {
//            when (it.type) {
//                ChatMessageEvent.Type.FETCH_ERROR -> {
//                    ExceptionDialog(it.errorMessage, this).show(
//                        childFragmentManager,
//                        ExceptionDialog.TAG
//                    )
//                    binding.llChatLoading.visibility = View.GONE
//                }
//                ChatMessageEvent.Type.FETCH -> {
//
//                }
//            }
//        })
    }


    private fun setupChatRecyclerView() {
//        chatRecyclerViewAdapter = ChatRecyclerViewAdapter()
//        binding.rvChat.adapter = chatRecyclerViewAdapter
//        layoutManager = LinearLayoutManager(this@ChatFragment.context)
//        layoutManager.orientation = LinearLayoutManager.VERTICAL
//        layoutManager.reverseLayout = true
//        binding.rvChat.layoutManager = layoutManager

    }


    private fun setupWebSocketLifeCycleEventObserver() {
//        viewModel.webSocketLifeCycleEvent.observe(viewLifecycleOwner, {
//            when (it.type) {
//                WebSocketEvent.Type.ERROR -> {
//                    ExceptionDialog(it.errorMessage, null).show(
//                        childFragmentManager,
//                        ExceptionDialog.TAG
//                    )
//                }
//            }
//        })
    }

    override fun onClickExceptionDialogCloseBtn() {
//        Navigation.findNavController(requireView())
//            .navigate(R.id.action_chatFragment_to_matchFragment)
    }

    override fun onResume() {
        super.onResume()
//        viewModel.connectChat()
    }

    override fun onPause() {
        super.onPause()
//        viewModel.disconnectChat()
    }


}