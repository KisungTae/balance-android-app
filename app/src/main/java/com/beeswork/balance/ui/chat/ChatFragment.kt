package com.beeswork.balance.ui.chat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentChatBinding
import com.beeswork.balance.internal.constant.BundleKey
import com.beeswork.balance.ui.common.ScopeFragment
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.mainviewpager.MainViewPagerFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.factory
import java.util.*


class ChatFragment : ScopeFragment(), KodeinAware, ErrorDialog.OnDismissListener {

    override val kodein by closestKodein()
    private val viewModelFactory: ((Long) -> ChatViewModelFactory) by factory()
    private lateinit var viewModel: ChatViewModel
    private lateinit var chatMessagePagingAdapter: ChatMessagePagingAdapter
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

        arguments?.let { arguments ->
            viewModel = ViewModelProvider(
                this,
                viewModelFactory(arguments.getLong(BundleKey.CHAT_ID))
            ).get(ChatViewModel::class.java)
            bindUI()
        } ?: kotlin.run {
            ErrorDialog(
                null,
                getString(R.string.chat_id_not_found_exception),
                null,
                this
            ).show(childFragmentManager, ErrorDialog.TAG)
        }
    }

    private fun bindUI() = launch {
        setupBackPressedDispatcherCallback()
        setupToolBar()
        setupSendBtnListener()
        setupEmoticonBtnListener()

        setupChatRecyclerView()
        setupChatMessagePagingData()

    }

    private fun populateMatchProfile() {
        CoroutineScope(Dispatchers.IO).launch {
            val
        }
    }

    private fun setupAsUnmatched() {
        binding.tvChatMatchedName.setTextColor(ContextCompat.getColor(requireContext(), R.color.TextGrey))
    }

    private fun setupEmoticonBtnListener() {
        binding.btnChatEmoticon.setOnClickListener {
//            val view = activity?.currentFocus
//            val methodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            methodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun setupSendBtnListener() {
        binding.btnChatMessageSend.setOnClickListener {
            viewModel.sendChatMessage(binding.etChatMessageBody.text.toString())
        }
    }

    private suspend fun setupChatMessagePagingData() {
        viewModel.initializeChatMessagePagingData().collectLatest {
            chatMessagePagingAdapter.submitData(it)
        }
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
                else -> false
            }
        }
        binding.btnChatBack.setOnClickListener { popBackToMatch() }
    }

    private fun setupChatRecyclerView() {
        chatMessagePagingAdapter = ChatMessagePagingAdapter()
        binding.rvChat.adapter = chatMessagePagingAdapter
        val layoutManager = LinearLayoutManager(this@ChatFragment.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        layoutManager.reverseLayout = true
        binding.rvChat.layoutManager = layoutManager
    }

    private fun popBackToMatch() {
        requireActivity().supportFragmentManager.popBackStack(MainViewPagerFragment.TAG, POP_BACK_STACK_INCLUSIVE)
    }

    override fun onDismiss() {
        popBackToMatch()
    }
}


