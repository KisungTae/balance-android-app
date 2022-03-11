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
import com.beeswork.balance.databinding.FragmentChatBinding
import com.beeswork.balance.databinding.SnackBarNewChatMessageBinding
import com.beeswork.balance.domain.uistate.chat.ChatMessageItemUIState
import com.beeswork.balance.internal.constant.*
import com.beeswork.balance.internal.util.*
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.common.PagingRefreshAdapter
import com.beeswork.balance.ui.dialog.ConfirmDialog
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.dialog.ReportDialog
import com.beeswork.balance.ui.mainviewpagerfragment.MainViewPagerFragment
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.factory
import java.lang.Exception
import java.util.*


class ChatFragment : BaseFragment(),
    KodeinAware,
    ErrorDialog.DismissListener,
    ChatMessagePagingAdapter.ChatMessageSentListener,
    ConfirmDialog.ConfirmDialogClickListener,
    ChatMoreMenuDialog.ChatMoreMenuDialogClickListener,
    ReportDialog.ReportDialogClickListener,
    ErrorDialog.RetryListener {

    override val kodein by closestKodein()
    private val viewModelFactory: ((UUID) -> ChatViewModelFactory) by factory()
    private lateinit var viewModel: ChatViewModel
    private lateinit var chatMessagePagingAdapter: ChatMessagePagingAdapter
    private lateinit var chatMessagePagingRefreshAdapter: PagingRefreshAdapter<ChatMessageItemUIState, ChatMessagePagingAdapter.ViewHolder>
    private lateinit var binding: FragmentChatBinding
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
        if (chatId != null) {
            viewModel = ViewModelProvider(this, viewModelFactory(chatId)).get(ChatViewModel::class.java)
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
        observeMatchLiveData()
        observeChatMessagePagingData()
        observeChatPageInvalidationLiveData()
        setupSendBtnListener()
        observeSendChatMessageUIStateLiveData()
        observeResendChatMessageUIStateLiveData()


        setupEmoticonBtnListener()
        observeReportMatchLiveData()
        observeUnmatchLiveData()
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
        chatMessagePagingAdapter = ChatMessagePagingAdapter(this, resources.displayMetrics.density)
        binding.rvChat.adapter = chatMessagePagingAdapter
        val layoutManager = LinearLayoutManager(this@ChatFragment.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        layoutManager.reverseLayout = true
        binding.rvChat.layoutManager = layoutManager
        binding.rvChat.itemAnimator = null
        chatMessagePagingRefreshAdapter = PagingRefreshAdapter(binding.rvChat, chatMessagePagingAdapter)
    }

    private suspend fun observeMatchLiveData() {
        viewModel.matchLiveData.await().observe(viewLifecycleOwner) { matchItemUIState ->
            if (matchItemUIState == null || matchItemUIState.unmatched) {
                setupAsUnmatched()
            } else {
                binding.tvChatSwipedName.text = matchItemUIState.swipedName ?: getString(R.string.unknown_user_name)
                if (matchItemUIState.swipedProfilePhotoKey != null) {
                    setupSwipedProfilePhoto(matchItemUIState.swipedId, matchItemUIState.swipedProfilePhotoKey)
                }
            }
        }
    }

    private fun observeChatMessagePagingData() {
        chatMessagePagingObserveJob?.cancel()
        registerAdapterDataObserver()
        chatMessagePagingObserveJob = lifecycleScope.launch {
            viewModel.getChatMessagePagingData().observe(viewLifecycleOwner) { pagingData ->
                chatMessagePagingRefreshAdapter.reset()
                lifecycleScope.launch {
                    chatMessagePagingAdapter.submitData(pagingData)
                }
            }
        }
    }

    private fun registerAdapterDataObserver() {
        chatMessagePagingAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                binding.rvChat.scrollToPosition(0)
                chatMessagePagingAdapter.unregisterAdapterDataObserver(this)
            }
        })
    }

    private suspend fun observeChatPageInvalidationLiveData() {
        viewModel.chatPageInvalidationLiveData.await().observe(viewLifecycleOwner) { chatPageInvalidationUIState ->
            if (binding.rvChat.canScrollVertically(1)) {
                if (chatPageInvalidationUIState?.scrollToBottom == true) {
                    observeChatMessagePagingData()
                } else {
                    if (chatPageInvalidationUIState?.body != null) {
                        showNewChatMessageSnackBar(chatPageInvalidationUIState.body)
                    }
                    chatMessagePagingAdapter.refresh()
                }
            } else {
                observeChatMessagePagingData()
            }
        }
    }

    private fun showNewChatMessageSnackBar(body: String) {
        val snackBarBinding = SnackBarNewChatMessageBinding.inflate(layoutInflater)
        val snackBar = SnackBarHelper.make(binding.clChatSnackBarPlaceHolder, Gravity.BOTTOM, 0, 0, snackBarBinding.root)
        snackBarBinding.tvSnackBarNewChatMessage.text = body
        snackBarBinding.llSnackBarChatMessage.setOnClickListener {
            binding.rvChat.scrollToPosition(0)
            snackBar.dismiss()
        }
        snackBar.show()
    }

    private fun setupSwipedProfilePhoto(swipedId: UUID, photoKey: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val profilePhotoEndPoint = EndPoint.ofPhoto(swipedId, photoKey)
                val file = Glide.with(requireContext()).downloadOnly().load(profilePhotoEndPoint).submit().get()
                if (file.exists()) {
                    withContext(Dispatchers.Main) {
                        val profilePhotoBitmap = BitmapFactory.decodeFile(file.path)
                        chatMessagePagingAdapter.setProfilePhoto(profilePhotoBitmap)
                    }
                }
            } catch (e: Exception) { }
        }
    }

    private fun setupSendBtnListener() {
        binding.btnChatMessageSend.setOnClickListener {
            viewModel.sendChatMessage(binding.etChatMessageBody.text.toString())
        }
    }

    private fun observeSendChatMessageUIStateLiveData() {
        viewModel.sendChatMessageUIStateLiveData.observeUIState(viewLifecycleOwner, activity) { uiState ->
            if (uiState.clearChatMessageInput) {
                binding.etChatMessageBody.setText("")
            }
            if (uiState.showError) {
                showSendChatMessageErrorDialog(uiState.exception)
            }
        }
    }

    private fun observeResendChatMessageUIStateLiveData() {
        viewModel.resendChatMessageUIStateLiveData.observeUIState(viewLifecycleOwner, activity) { uiState ->
            if (uiState.showError) {
                showSendChatMessageErrorDialog(uiState.exception)
            }
        }
    }

    private fun showSendChatMessageErrorDialog(exception: Throwable?) {
        val title = getString(R.string.error_title_send_chat_message)
        val message = MessageSource.getMessage(requireContext(), exception)
        ErrorDialog.show(title, message, childFragmentManager)
    }

    override fun onResendChatMessage(position: Int) {
        chatMessagePagingAdapter.getChatMessage(position)?.let { chatMessageItemUIState ->
            if (chatMessageItemUIState.tag != null) {
                viewModel.resendChatMessage(chatMessageItemUIState.tag)
            }
        }
    }



















    private fun observeUnmatchLiveData() {
        viewModel.unmatchLiveData.observeResource(viewLifecycleOwner, activity) { resource ->
            when {
                resource.isSuccess() -> popBackStack(MainViewPagerFragment.TAG)
                resource.isLoading() -> showLoading()
                resource.isError() -> showUnmatchErrorDialog(resource.exception)
            }
        }
    }

    private fun showUnmatchErrorDialog(exception: Throwable?) {
        hideLoading()
        val title = getString(R.string.error_title_unmatch)
        val message = MessageSource.getMessage(requireContext(), exception)
        ErrorDialog.show(title, message, RequestCode.UNMATCH, this, childFragmentManager)
    }

    private fun showLoading() {
        binding.llChatLoading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.llChatLoading.visibility = View.GONE
    }

    private fun observeReportMatchLiveData() {
        viewModel.reportMatchLiveData.observeResource(viewLifecycleOwner, activity) { resource ->
            when {
                resource.isSuccess() -> popBackStack(MainViewPagerFragment.TAG)
                resource.isLoading() -> getReportDialog()?.showLoading()
                resource.isError() -> showReportMatchErrorDialog(resource.exception)
            }
        }
    }

    private fun showReportMatchErrorDialog(exception: Throwable?) {
        getReportDialog()?.hideLoading()
        val title = getString(R.string.error_title_report)
        val message = MessageSource.getMessage(requireContext(), exception)
        ErrorDialog.show(title, message, RequestCode.REPORT_MATCH, this, childFragmentManager)
    }

    private fun getReportDialog(): ReportDialog? {
        return childFragmentManager.findFragmentByTag(ReportDialog.TAG)?.let {
            return@let it as ReportDialog
        }
    }

    private fun observeSendChatMessageMediatorLiveData() {
        viewModel.sendChatMessageMediatorLiveData.observeResource(viewLifecycleOwner, activity) { resource ->
            if (resource.isError()) {
                if (resource.isExceptionCodeEqualTo(ExceptionCode.MATCH_UNMATCHED_EXCEPTION)) {
                    setupAsUnmatched()
                }
//                showSendChatMessageErrorDialog(resource.exception)
            }
        }
    }




    private fun setupAsUnmatched() {
        binding.tvChatSwipedName.setTextColor(ContextCompat.getColor(requireContext(), R.color.TextGrey))
        binding.tvChatSwipedName.text = getString(R.string.unknown_user_name)
        binding.etChatMessageBody.isFocusableInTouchMode = false
        binding.etChatMessageBody.isFocusable = false
        binding.btnChatMessageSend.isEnabled = false
        chatMessagePagingAdapter.setProfilePhoto(null)
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
        popBackStack(MainViewPagerFragment.TAG)
    }



    override fun onDeleteChatMessage(position: Int) {
        chatMessagePagingAdapter.getChatMessage(position)?.let {
            val confirmDialog = ConfirmDialog(
                getString(R.string.confirm_dialog_delete_button_title),
                RequestCode.DELETE_CHAT_MESSAGE,
                this
            )
            val arguments = Bundle()
//            arguments.putLong(BundleKey.CHAT_MESSAGE_KEY, it.key)
            confirmDialog.arguments = arguments
            confirmDialog.show(childFragmentManager, ConfirmDialog.TAG)
        }
    }

    private fun setupBackBtn() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                popBackStack(MainViewPagerFragment.TAG)
            }
        })
        binding.btnChatBack.setOnClickListener { popBackStack(MainViewPagerFragment.TAG) }
    }


    override fun onConfirm(requestCode: Int, argument: Bundle?) {
        when (requestCode) {
            RequestCode.DELETE_CHAT_MESSAGE -> {
                argument?.let { viewModel.deleteChatMessage(it.getLong(BundleKey.CHAT_MESSAGE_KEY)) }
            }
        }
    }

    override fun onUnmatch() {
        viewModel.unmatch()
    }

    override fun onReportMatch() {
        ReportDialog(this).show(childFragmentManager, ReportDialog.TAG)
    }

    override fun submitReport(reportReason: ReportReason, description: String) {
        viewModel.reportMatch(reportReason, description)
    }

    override fun onRetry(requestCode: Int?) {
        when (requestCode) {
            RequestCode.REPORT_MATCH -> getReportDialog()?.clickSubmitButton()
            RequestCode.UNMATCH -> onUnmatch()
        }
    }
}


