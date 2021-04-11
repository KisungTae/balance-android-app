package com.beeswork.balance.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentChatBinding
import com.beeswork.balance.internal.constant.BundleKey
import com.beeswork.balance.internal.util.safeLet
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.common.PagingRefreshAdapter
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.mainviewpager.MainViewPagerFragment
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.factory
import java.util.*


class ChatFragment : BaseFragment(),
    KodeinAware,
    ErrorDialog.OnDismissListener {

    override val kodein by closestKodein()
    private val viewModelFactory: ((ChatViewModelFactoryParameter) -> ChatViewModelFactory) by factory()
    private lateinit var viewModel: ChatViewModel
    private lateinit var chatMessagePagingAdapter: ChatMessagePagingAdapter
    private lateinit var chatMessagePagingRefreshAdapter: PagingRefreshAdapter<ChatMessageDomain, ChatMessagePagingAdapter.ViewHolder>
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
        setupBackPressedDispatcherCallback()
        safeLet(arguments, arguments?.getString(BundleKey.MATCHED_ID)) { arguments, matchedIdString ->
            val matchedId = UUID.fromString(matchedIdString)
            val chatViewModelFactoryParameter = ChatViewModelFactoryParameter(
                arguments.getLong(BundleKey.CHAT_ID),
                matchedId
            )

            viewModel = ViewModelProvider(
                this,
                viewModelFactory(chatViewModelFactoryParameter)
            ).get(ChatViewModel::class.java)

            bindUI(
                matchedId,
                arguments.getString(BundleKey.MATCHED_NAME),
                arguments.getString(BundleKey.MATCHED_REP_PHOTO_KEY),
                arguments.getBoolean(BundleKey.MATCH_VALID)
            )
        } ?: ErrorDialog(null, getString(R.string.error_title_chat_id_not_found), "", null, this).show(
            childFragmentManager,
            ErrorDialog.TAG
        )
    }

    private fun bindUI(
        matchedId: UUID,
        matchedName: String?,
        matchedRepPhotoKey: String?,
        matchValid: Boolean
    ) = lifecycleScope.launch {
        setupBackPressedDispatcherCallback()
        setupToolBar(matchedName)
        setupSendBtnListener()
        setupEmoticonBtnListener()
        setupSendChatMessageObserver()
        setupChatRecyclerView()
//        setupRepPhoto(matchedRepPhotoKey?.let { EndPoint.ofPhotoBucket(matchedId, it) })
        if (!matchValid) setupAsUnmatched()
        setupChatMessagePagingRefreshObserver()
        setupChatMessagePagingData()
    }

    private fun setupSendChatMessageObserver() {
        viewModel.sendChatMessageLiveData.observe(viewLifecycleOwner, {
            println("sendChatMessageLiveData.observe")
            if (it.isError()) ErrorDialog(
                it.error,
                getString(R.string.error_title_send_chat_message),
                it.errorMessage,
                null,
                null
            ).show(childFragmentManager, ErrorDialog.TAG)
        })
    }

    private fun setupChatMessagePagingRefreshObserver() {
        viewModel.chatMessagePagingRefreshLiveData.observe(viewLifecycleOwner, {
            //TODO: if data is not null, then check if scroll is bottom if not then new message alert in chat

            chatMessagePagingRefreshAdapter.refresh()
        })
    }

    private fun setupAsUnmatched() {
        binding.tvChatMatchedName.setTextColor(ContextCompat.getColor(requireContext(), R.color.TextGrey))
        binding.llChatInputWrapper.visibility = View.GONE
    }


    private fun setupSendBtnListener() {
        binding.btnChatMessageSend.setOnClickListener {
            viewModel.sendChatMessage(binding.etChatMessageBody.text.toString().trim())
        }
    }

    private suspend fun setupChatMessagePagingData() {
        registerAdapterDataObserver()
        viewModel.initChatMessagePagingData().observe(viewLifecycleOwner) {
            lifecycleScope.launch { chatMessagePagingAdapter.submitData(it) }
        }
    }

    private fun registerAdapterDataObserver() {
        chatMessagePagingAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                viewModel.synchronizeMatch()
                chatMessagePagingAdapter.unregisterAdapterDataObserver(this)
            }
        })
    }

    private fun setupBackPressedDispatcherCallback() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                popBackToMatch()
            }
        })
    }

    private fun setupToolBar(matchedName: String?) {
        binding.tvChatMatchedName.text = matchedName ?: ""
        binding.tbChat.inflateMenu(R.menu.chat_tool_bar)
        binding.tbChat.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.miChatLeave -> {
                    chatMessagePagingRefreshAdapter.refresh()
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
        binding.rvChat.itemAnimator = null
        chatMessagePagingRefreshAdapter = PagingRefreshAdapter(binding.rvChat, chatMessagePagingAdapter)
    }

    private fun setupEmoticonBtnListener() {
        binding.btnChatEmoticon.setOnClickListener {
//            val view = activity?.currentFocus
//            val methodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            methodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private suspend fun setupRepPhoto(repPhotoEndPoint: String?) = withContext(Dispatchers.IO) {
        repPhotoEndPoint?.let { repPhotoEndPoint ->
            runCatching {
                val file = Glide.with(requireContext()).downloadOnly().load(repPhotoEndPoint).submit().get()
                if (file.exists()) withContext(Dispatchers.Main) {
                    chatMessagePagingAdapter.onRepPhotoLoaded(repPhotoEndPoint)
                }
            }.getOrNull()
        }
    }

    private fun popBackToMatch() {
        requireActivity().supportFragmentManager.popBackStack(MainViewPagerFragment.TAG, POP_BACK_STACK_INCLUSIVE)
    }

    override fun onDismiss() {
        popBackToMatch()
    }
}


