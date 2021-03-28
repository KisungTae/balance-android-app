package com.beeswork.balance.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentChatBinding
import com.beeswork.balance.internal.constant.BundleKey
import com.beeswork.balance.internal.constant.DateTimePattern
import com.beeswork.balance.ui.common.ScopeFragment
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.mainviewpager.MainViewPagerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.factory
import org.threeten.bp.DayOfWeek
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import java.util.*


class ChatFragment : ScopeFragment(), KodeinAware, ErrorDialog.OnDismissListener {

    override val kodein by closestKodein()
    private val viewModelFactory: ((Long) -> ChatViewModelFactory) by factory()
    private lateinit var viewModel: ChatViewModel
    private lateinit var chatMessagePagingAdapter: ChatMessagePagingAdapter
    private lateinit var binding: FragmentChatBinding
    private var searchJob: Job? = null

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
            ErrorDialog(
                null,
                getString(R.string.chat_id_not_found_exception),
                null,
                this
            ).show(childFragmentManager, ErrorDialog.TAG)
        }
    }

    private fun bindUI() {
        setupBackPressedDispatcherCallback()
        setupToolBar()
        setupChatRecyclerView()
        search("")
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
//                    viewModel.test()
                    showSearchToolBar()
                    true
                }
                else -> false
            }
        }
        binding.btnChatSearchClose.setOnClickListener { hideSearchToolBar() }
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

    private fun search(keyword: String) {
        searchJob?.cancel()
        searchJob = launch {
            viewModel.initializeChatPagingData(keyword.trim()).collectLatest {
                chatMessagePagingAdapter.submitData(it)
            }
        }
    }

    private fun hideSearchToolBar() {
        binding.tbChatSearch.visibility = View.GONE
        binding.tbChat.visibility = View.VISIBLE
        binding.etChatSearch.setText("")
    }

    private fun showSearchToolBar() {
        binding.tbChat.visibility = View.GONE
        binding.tbChatSearch.visibility = View.VISIBLE
    }

    private fun popBackToMatch() {
        requireActivity().supportFragmentManager.popBackStack(MainViewPagerFragment.TAG, POP_BACK_STACK_INCLUSIVE)
    }

    override fun onDismiss() {
        popBackToMatch()
    }
}