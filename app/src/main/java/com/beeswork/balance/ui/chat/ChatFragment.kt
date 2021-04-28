package com.beeswork.balance.ui.chat

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.database.response.ChatMessagePagingRefresh
import com.beeswork.balance.databinding.FragmentChatBinding
import com.beeswork.balance.databinding.SnackBarNewChatMessageBinding
import com.beeswork.balance.internal.constant.BundleKey
import com.beeswork.balance.internal.constant.RequestCode
import com.beeswork.balance.internal.util.safeLet
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.common.PagingRefreshAdapter
import com.beeswork.balance.ui.dialog.ConfirmDialog
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.mainviewpager.MainViewPagerFragment
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.factory
import java.util.*


class ChatFragment : BaseFragment(),
    KodeinAware,
    ErrorDialog.OnDismissListener,
    ChatMessagePagingAdapter.ChatMessageSentListener,
    ConfirmDialog.ConfirmDialogClickListener {

    override val kodein by closestKodein()
    private val viewModelFactory: ((ChatViewModelFactoryParameter) -> ChatViewModelFactory) by factory()
    private lateinit var viewModel: ChatViewModel
    private lateinit var chatMessagePagingAdapter: ChatMessagePagingAdapter
    private lateinit var chatMessagePagingRefreshAdapter: PagingRefreshAdapter<ChatMessageDomain, ChatMessagePagingAdapter.ViewHolder>
    private lateinit var binding: FragmentChatBinding
    private var newChatMessageSnackBar: Snackbar? = null
    private var job: Job? = null

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
                arguments.getBoolean(BundleKey.UNMATCHED)
            )
        } ?: ErrorDialog(null, getString(R.string.error_title_chat_id_not_found), "", null, null, this).show(
            childFragmentManager,
            ErrorDialog.TAG
        )
    }

    private fun bindUI(
        matchedId: UUID,
        matchedName: String?,
        matchedRepPhotoKey: String?,
        unmatched: Boolean
    ) = lifecycleScope.launch {
        setupBackPressedDispatcherCallback()
        setupToolBar(matchedName)
        setupSendBtnListener()
        setupEmoticonBtnListener()
        setupSendChatMessageMediatorLiveDataObserver()
        setupChatRecyclerView()
//        setupRepPhoto(matchedRepPhotoKey?.let { EndPoint.ofPhotoBucket(matchedId, it) })
        if (unmatched) setupAsUnmatched()
        setupChatMessagePagingRefreshObserver()
        setupChatMessagePagingData()
    }

    private fun setupSendChatMessageMediatorLiveDataObserver() {
        viewModel.sendChatMessageMediatorLiveData.observe(viewLifecycleOwner, {
            if (it.isError()) ErrorDialog(
                it.error,
                getString(R.string.error_title_send_chat_message),
                it.errorMessage,
                null,
                null,
                null
            ).show(childFragmentManager, ErrorDialog.TAG)
        })
    }

    private fun setupChatMessagePagingRefreshObserver() {
        viewModel.chatMessagePagingRefreshMediatorLiveData.observe(viewLifecycleOwner, {
            //TODO: if data is not null, then check if scroll is bottom if not then new message alert in chat
            when (it.type) {
                ChatMessagePagingRefresh.Type.SEND -> {
                    binding.etChatMessageBody.setText("")
                }
                ChatMessagePagingRefresh.Type.RECEIVED -> {
                    println("received new chat message")
                    setupChatMessagePagingData()
                }
                else -> {
                }
            }
            chatMessagePagingRefreshAdapter.refresh()
        })
    }

    private fun setupAsUnmatched() {
        binding.tvChatMatchedName.setTextColor(ContextCompat.getColor(requireContext(), R.color.TextGrey))
    }

    private fun setupSendBtnListener() {
        binding.btnChatMessageSend.setOnClickListener {
            viewModel.sendChatMessage(binding.etChatMessageBody.text.toString().trim())
        }
    }

//    private suspend fun setupChatMessagePagingData() {
//        job?.cancel()
//        registerAdapterDataObserver()
//        job = lifecycleScope.launch {
//            viewModel.initChatMessagePagingData().observe(viewLifecycleOwner) {
//                lifecycleScope.launch { chatMessagePagingAdapter.submitData(it) }
//            }
//        }
//    }

    private fun setupChatMessagePagingData() {
        job?.cancel()
        registerAdapterDataObserver()
        job = lifecycleScope.launch {
            viewModel.initChatMessagePagingData().observe(viewLifecycleOwner) {
                chatMessagePagingRefreshAdapter.reset()
                lifecycleScope.launch { chatMessagePagingAdapter.submitData(it) }
            }
        }
    }

    private fun registerAdapterDataObserver() {
        chatMessagePagingAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                viewModel.synchronizeMatch()
                binding.rvChat.scrollToPosition(0)
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
                    viewModel.test()
//                    chatMessagePagingRefreshAdapter.refresh()
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
        chatMessagePagingAdapter = ChatMessagePagingAdapter(this)
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

    override fun onResendChatMessage(position: Int) {
        chatMessagePagingAdapter.getChatMessage(position)?.let {
            viewModel.resendChatMessage(it.key)
        }
    }

    override fun onDeleteChatMessage(position: Int) {
        chatMessagePagingAdapter.getChatMessage(position)?.let {
            val confirmDialog = ConfirmDialog(
                getString(R.string.confirm_dialog_delete_button_title),
                RequestCode.DELETE_CHAT_MESSAGE,
                this
            )
            val arguments = Bundle()
            arguments.putLong(BundleKey.CHAT_MESSAGE_KEY, it.key)
            confirmDialog.arguments = arguments
            confirmDialog.show(childFragmentManager, ConfirmDialog.TAG)
        }
    }

    private fun showNewChatMessageSnackBar(body: String) {
        val snackBar = Snackbar.make(requireView(), "", Snackbar.LENGTH_SHORT)
        snackBar.view.setBackgroundColor(Color.TRANSPARENT)

        val binding = SnackBarNewChatMessageBinding.inflate(layoutInflater)
        binding.tvSnackBarNewChatMessage.text = body
        binding.llSnackBarChatMessage.setOnClickListener {
            newChatMessageSnackBar?.dismiss()
        }
        val snackBarLayout = snackBar.view as Snackbar.SnackbarLayout
        snackBarLayout.addView(binding.root, 0)
        snackBarLayout.setPadding(10, 0, 10, 150)

        newChatMessageSnackBar?.dismiss()
        newChatMessageSnackBar = snackBar
        snackBar.show()
    }

    override fun onConfirm(requestCode: Int, argument: Bundle?) {
        when (requestCode) {
            RequestCode.DELETE_CHAT_MESSAGE -> {
                argument?.let { viewModel.deleteChatMessage(it.getLong(BundleKey.CHAT_MESSAGE_KEY)) }
            }
        }
    }
}


