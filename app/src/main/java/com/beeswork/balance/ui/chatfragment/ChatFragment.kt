package com.beeswork.balance.ui.chatfragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.network.service.stomp.WebSocketStatus
import com.beeswork.balance.databinding.FragmentChatBinding
import com.beeswork.balance.databinding.SnackBarNewChatMessageBinding
import com.beeswork.balance.domain.uistate.chat.ChatMessageItemUIState
import com.beeswork.balance.internal.constant.*
import com.beeswork.balance.internal.exception.WebSocketDisconnectedException
import com.beeswork.balance.internal.util.*
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.common.PagingInitialPageAdapter
import com.beeswork.balance.ui.common.PagingRefreshAdapter
import com.beeswork.balance.ui.dialog.ConfirmDialog
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.reportdialog.ReportDialog
import com.beeswork.balance.ui.mainviewpagerfragment.MainViewPagerFragment
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.factory
import java.lang.Exception
import java.util.*


class ChatFragment : BaseFragment(),
    KodeinAware,
    ErrorDialog.DismissListener,
    ChatMessagePagingDataAdapter.ChatMessageListener,
    ConfirmDialog.ConfirmDialogClickListener,
    ChatMoreMenuDialog.ChatMoreMenuDialogClickListener,
    ReportDialog.ReportDialogListener,
    ErrorDialog.RetryListener {

    override val kodein by closestKodein()
    private val viewModelFactory: ((ChatViewModelParameter) -> ChatViewModelFactory) by factory()
    private lateinit var viewModel: ChatViewModel
    private lateinit var chatMessagePagingDataAdapter: ChatMessagePagingDataAdapter
    private lateinit var chatMessagePagingRefreshDataAdapter: PagingRefreshAdapter<ChatMessageItemUIState, ChatMessagePagingDataAdapter.ViewHolder>
    private lateinit var chatPagingInitialPageDataAdapter: PagingInitialPageAdapter<ChatMessageItemUIState, ChatMessagePagingDataAdapter.ViewHolder>
    private lateinit var binding: FragmentChatBinding
    private lateinit var swipedId: UUID
    private var chatMessagePagingObserveJob: Job? = null


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
        setupBackBtn()
        val chatId = Converter.toUUID(arguments?.getString(BundleKey.CHAT_ID))
        val swipedId = Converter.toUUID(arguments?.getString(BundleKey.SWIPED_ID))
        if (chatId != null && swipedId != null) {
            val chatViewModelParameter = ChatViewModelParameter(chatId, swipedId)
            this.swipedId = swipedId
            viewModel = ViewModelProvider(this, viewModelFactory(chatViewModelParameter)).get(ChatViewModel::class.java)
            bindUI()
        } else {
            val title = getString(R.string.error_title_open_chat)
            val message = getString(R.string.error_title_chat_id_not_found)
            ErrorDialog.show(title, message, this, childFragmentManager)
        }
    }

    private fun bindUI() = lifecycleScope.launch {
        setupToolBar()
        setupChatRecyclerView()
        setupChatPagingInitialPageAdapter()
        observeMatchLiveData()
        observeChatMessagePagingData()
        observeChatPageInvalidationLiveData()
        setupSendBtnListener()
        observeSendChatMessageUIStateLiveData()
        observeResendChatMessageUIStateLiveData()
        observeWebSocketEventLiveData()
        setupEmoticonBtnListener()
        observeUnmatchLiveData()
        viewModel.syncMatch()
    }

    private suspend fun observeWebSocketEventLiveData() {
        viewModel.webSocketEventUIStateLiveData.await().observe(viewLifecycleOwner) { webSocketEventUIState ->
            if (webSocketEventUIState.webSocketStatus == WebSocketStatus.STOMP_CONNECTED) {
                Snackbar.make(requireView(), getString(R.string.message_server_connected), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.close)) {}.show()
            }
        }
    }

    private fun setupToolBar() {
        binding.tbChat.inflateMenu(R.menu.chat_tool_bar)
        binding.tbChat.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.miChatMore -> showMoreMenu()
                else -> false
            }
        }
    }

    private fun setupChatRecyclerView() {
        chatMessagePagingDataAdapter = ChatMessagePagingDataAdapter(this)
//        footerLoadStateAdapter = BalanceLoadStateAdapter(chatMessagePagingDataAdapter::retry)
//        binding.rvChat.adapter = chatMessagePagingDataAdapter.withLoadStateFooter(
//            footer = footerLoadStateAdapter
//        )
        val layoutManager = LinearLayoutManager(this@ChatFragment.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        layoutManager.reverseLayout = true
        binding.rvChat.layoutManager = layoutManager
        binding.rvChat.itemAnimator = null
        chatMessagePagingRefreshDataAdapter = PagingRefreshAdapter(binding.rvChat, chatMessagePagingDataAdapter)
    }

    private suspend fun observeMatchLiveData() {
        viewModel.matchUIStateLiveData.await().observe(viewLifecycleOwner) { matchItemUIState ->
            if (matchItemUIState == null || matchItemUIState.unmatched) {
                setupAsUnmatched()
            } else {
                binding.tvChatSwipedName.text = matchItemUIState.swipedName ?: getString(R.string.unknown_user_name)
                if (matchItemUIState.swipedProfilePhotoUrl != null) {
                    setupSwipedProfilePhoto(matchItemUIState.swipedProfilePhotoUrl)
                }
            }
        }
    }

    private fun observeChatMessagePagingData() {
        chatMessagePagingObserveJob?.cancel()
        registerAdapterDataObserver()
        chatMessagePagingObserveJob = lifecycleScope.launch {
            viewModel.getChatMessagePagingData().observe(viewLifecycleOwner) { pagingData ->
                chatMessagePagingRefreshDataAdapter.reset()
                lifecycleScope.launch {
                    chatMessagePagingDataAdapter.submitData(pagingData)
                }
            }
        }
    }

    private fun registerAdapterDataObserver() {
        chatMessagePagingDataAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                binding.rvChat.scrollToPosition(0)
                chatMessagePagingDataAdapter.unregisterAdapterDataObserver(this)
            }
        })
    }

    private suspend fun observeChatPageInvalidationLiveData() {
        viewModel.chatPageInvalidationUIStateLiveData.await().observe(viewLifecycleOwner) { chatPageInvalidationUIState ->
            if (binding.rvChat.canScrollVertically(1)) {
                if (chatPageInvalidationUIState?.scrollToBottom == true) {
                    observeChatMessagePagingData()
                } else {
                    if (chatPageInvalidationUIState?.body != null) {
                        showNewChatMessageSnackBar(chatPageInvalidationUIState.body)
                    }
                    chatMessagePagingDataAdapter.refresh()
                }
            } else {
                observeChatMessagePagingData()
            }
            viewModel.syncMatch()
        }
    }

    private fun showNewChatMessageSnackBar(body: String) {
        val snackBarBinding = SnackBarNewChatMessageBinding.inflate(layoutInflater)
        val snackBar = SnackBarHelper.make(binding.clChatSnackBarPlaceHolder, Gravity.BOTTOM, 0, 0, snackBarBinding.root)
        snackBarBinding.tvSnackBarNewChatMessage.text = body
        snackBarBinding.llSnackBarChatMessage.setOnClickListener {
            observeChatMessagePagingData()
            snackBar.dismiss()
        }
        snackBar.show()
    }

    private fun setupSwipedProfilePhoto(swipedProfilePhotoUrl: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val file = Glide.with(requireContext()).downloadOnly().load(swipedProfilePhotoUrl).submit().get()
                if (file.exists()) {
                    withContext(Dispatchers.Main) {
                        val profilePhotoBitmap = BitmapFactory.decodeFile(file.path)
                        chatMessagePagingDataAdapter.setProfilePhoto(profilePhotoBitmap)
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun setupSendBtnListener() {
        binding.btnChatMessageSend.setOnClickListener {
            viewModel.sendChatMessage(binding.etChatMessageBody.text.toString())
        }
    }

    private fun observeSendChatMessageUIStateLiveData() {
        viewModel.sendChatMessageUIStateLiveData.observe(viewLifecycleOwner) { uiState ->
            if (uiState.clearChatMessageInput) {
                binding.etChatMessageBody.setText("")
            }
            if (uiState.showError) {
                showSendChatMessageErrorDialog(uiState.exception)
            }
        }
    }

    private fun observeResendChatMessageUIStateLiveData() {
        viewModel.resendChatMessageUIStateLiveData.observe(viewLifecycleOwner) { uiState ->
            if (uiState.showError) {
                showSendChatMessageErrorDialog(uiState.exception)
            }
        }
    }

    private fun showSendChatMessageErrorDialog(exception: Throwable?) {
        val title = getString(R.string.error_title_send_chat_message)
        val message = MessageSource.getMessage(exception)
        if (exception is WebSocketDisconnectedException) {
            val retryBtnTitle = resources.getString(R.string.title_connect_to_web_socket)
            ErrorDialog.show(title, message, retryBtnTitle, RequestCode.CONNECT_TO_STOMP, this, childFragmentManager)
        } else {
            ErrorDialog.show(title, message, childFragmentManager)
        }
    }

    override fun onResendChatMessage(position: Int) {
        chatMessagePagingDataAdapter.getChatMessage(position)?.let { chatMessageItemUIState ->
            if (chatMessageItemUIState.tag != null) {
                viewModel.resendChatMessage(chatMessageItemUIState.tag)
            }
        }
    }

    private fun setupChatPagingInitialPageAdapter() {
        binding.btnChatRetry.setOnClickListener {
            chatMessagePagingDataAdapter.retry()
        }
        chatPagingInitialPageDataAdapter = PagingInitialPageAdapter(
            chatMessagePagingDataAdapter,
            binding.llChatInitialLoadingPage,
            binding.llChatInitialErrorPage,
            null,
            binding.tvChatErrorMessage,
            requireContext()
        )
        lifecycleScope.launch {
            chatMessagePagingDataAdapter.loadStateFlow.collect { loadState ->
                chatPagingInitialPageDataAdapter.updateUI(loadState)
            }
        }
    }


    private fun observeUnmatchLiveData() {
        viewModel.unmatchLiveData.observeUIState(viewLifecycleOwner, activity) { uiState ->
            when {
                uiState.unmatched -> Navigator.popBackStack(activity, MainViewPagerFragment.TAG)
                uiState.showLoading -> {
                    binding.llChatLoading.visibility = View.VISIBLE
                }
                uiState.showError -> {
                    binding.llChatLoading.visibility = View.GONE
                    showUnmatchErrorDialog(uiState.exception)
                }
            }
        }
    }

    private fun showUnmatchErrorDialog(exception: Throwable?) {
        val title = getString(R.string.error_title_unmatch)
        val message = MessageSource.getMessage(exception)
        ErrorDialog.show(title, message, RequestCode.UNMATCH, this, childFragmentManager)
    }

    private fun getReportDialog(): ReportDialog? {
        return childFragmentManager.findFragmentByTag(ReportDialog.TAG)?.let {
            return@let it as ReportDialog
        }
    }

    private fun setupAsUnmatched() {
        binding.tvChatSwipedName.setTextColor(ContextCompat.getColor(requireContext(), R.color.TextGrey))
        binding.tvChatSwipedName.text = getString(R.string.unknown_user_name)
        binding.etChatMessageBody.isFocusableInTouchMode = false
        binding.etChatMessageBody.isFocusable = false
        binding.btnChatMessageSend.isEnabled = false
        chatMessagePagingDataAdapter.setProfilePhoto(null)
    }


    private fun showMoreMenu(): Boolean {
        ChatMoreMenuDialog(this).show(childFragmentManager, ChatMoreMenuDialog.TAG)
        return true
    }


    private fun setupEmoticonBtnListener() {
        binding.btnChatEmoticon.setOnClickListener {
//            val view = activity?.currentFocus
//            val methodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            methodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }


    override fun onDismissErrorDialog(id: UUID?) {
        Navigator.popBackStack(activity, MainViewPagerFragment.TAG)
    }


    override fun onDeleteChatMessage(position: Int) {
        chatMessagePagingDataAdapter.getChatMessage(position)?.let { chatMessageItemUIState ->
            val confirmDialog = ConfirmDialog(
                getString(R.string.confirm_dialog_delete_button_title),
                RequestCode.DELETE_CHAT_MESSAGE,
                this
            )
            val arguments = Bundle()
            arguments.putString(BundleKey.CHAT_MESSAGE_TAG, chatMessageItemUIState.tag.toString())
            confirmDialog.arguments = arguments
            confirmDialog.show(childFragmentManager, ConfirmDialog.TAG)
        }
    }

    private fun setupBackBtn() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireContext().hideKeyboard(requireView())
                Navigator.popBackStack(activity, MainViewPagerFragment.TAG)
            }
        })
        binding.btnChatBack.setOnClickListener {
            requireContext().hideKeyboard(requireView())
            Navigator.popBackStack(activity, MainViewPagerFragment.TAG)
        }
    }


    override fun onConfirm(requestCode: Int, argument: Bundle?) {
        when (requestCode) {
            RequestCode.DELETE_CHAT_MESSAGE -> {
                val chatMessageTag = Converter.toUUID(argument?.getString(BundleKey.CHAT_MESSAGE_TAG))
                if (chatMessageTag != null) {
                    viewModel.deleteChatMessage(chatMessageTag)
                }
            }
        }
    }

    override fun onUnmatch() {
        viewModel.unmatch()
    }

    override fun onReportMatch() {
        ReportDialog(this@ChatFragment, ReportType.REPORT_MATCH, swipedId).show(childFragmentManager, ReportDialog.TAG)
    }

    override fun onRetry(requestCode: Int?) {
        when (requestCode) {
            RequestCode.UNMATCH -> onUnmatch()
            RequestCode.CONNECT_TO_STOMP -> viewModel.connectToStomp()
        }
    }

    override fun onReportSubmitted() {
        Navigator.popBackStack(activity, MainViewPagerFragment.TAG)
    }
}


