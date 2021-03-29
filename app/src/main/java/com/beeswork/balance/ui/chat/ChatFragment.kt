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
    private var matchValid: Boolean = false

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
            matchValid = arguments.getBoolean(BundleKey.MATCH_VALID)
            bindUI(arguments.getString(BundleKey.MATCHED_NAME) ?: "")
        } ?: kotlin.run {
            ErrorDialog(
                null,
                getString(R.string.chat_id_not_found_exception),
                null,
                this
            ).show(childFragmentManager, ErrorDialog.TAG)
        }
    }

    private fun bindUI(matchedName: String) = launch {
        setupBackPressedDispatcherCallback()
        setupToolBar()
        setupChatRecyclerView()
        setupSendBtnListener()
        setupEmoticonBtnListener()
        setupMatchedName(matchedName)
        setupChatMessagePagingData()

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
            if (matchValid) {
                viewModel.sendChatMessage(binding.etChatMessageBody.text.toString())
                println("binding.etChatMessageBody.text.toString(): ${binding.etChatMessageBody.text}")
                binding.etChatMessageBody.setText("")
            }
        }
    }

    private fun setupMatchedName(matchedName: String) {
        if (!matchValid) binding.tvChatMatchedName.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.TextGrey
            )
        )
        binding.tvChatMatchedName.text = matchedName
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



// select * from chatMessage where chatId = 1 order by case when id is null then 1 else 0 end desc, id desc, `key` desc