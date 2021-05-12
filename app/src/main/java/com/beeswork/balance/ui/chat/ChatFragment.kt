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
import com.beeswork.balance.data.database.repository.chat.ChatMessageInvalidation
import com.beeswork.balance.databinding.FragmentChatBinding
import com.beeswork.balance.databinding.SnackBarNewChatMessageBinding
import com.beeswork.balance.internal.constant.BundleKey
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.constant.ReportReason
import com.beeswork.balance.internal.constant.RequestCode
import com.beeswork.balance.internal.util.SnackBarHelper
import com.beeswork.balance.internal.util.safeLet
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.common.PagingRefreshAdapter
import com.beeswork.balance.ui.dialog.ConfirmDialog
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.dialog.ReportDialog
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
    ConfirmDialog.ConfirmDialogClickListener,
    ChatMoreMenuDialog.ChatMoreMenuDialogClickListener,
    ReportDialog.ReportDialogClickListener,
    ErrorDialog.OnRetryListener {

    override val kodein by closestKodein()
    private val viewModelFactory: ((ChatViewModelFactoryParameter) -> ChatViewModelFactory) by factory()
    private lateinit var viewModel: ChatViewModel
    private lateinit var chatMessagePagingAdapter: ChatMessagePagingAdapter
    private lateinit var chatMessagePagingRefreshAdapter: PagingRefreshAdapter<ChatMessageDomain, ChatMessagePagingAdapter.ViewHolder>
    private lateinit var binding: FragmentChatBinding
    private var newChatMessageSnackBar: Snackbar? = null
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
        setupBackPressedDispatcherCallback()
        safeLet(arguments, arguments?.getString(BundleKey.SWIPED_ID)) { arguments, swipedIdString ->
            val swipedId = UUID.fromString(swipedIdString)
            val chatViewModelFactoryParameter = ChatViewModelFactoryParameter(
                arguments.getLong(BundleKey.CHAT_ID),
                swipedId
            )

            viewModel = ViewModelProvider(
                this,
                viewModelFactory(chatViewModelFactoryParameter)
            ).get(ChatViewModel::class.java)

            bindUI(
                swipedId,
                arguments.getString(BundleKey.SWIPED_NAME),
                arguments.getString(BundleKey.SWIPED_PROFILE_PHOTO_KEY),
                arguments.getBoolean(BundleKey.UNMATCHED)
            )
        } ?: showErrorDialog(getString(R.string.error_title_chat_id_not_found), "", this)
    }

    private fun bindUI(
        swipedId: UUID,
        swipedName: String?,
        swipedProfilePhotoKey: String?,
        unmatched: Boolean
    ) = lifecycleScope.launch {
        setupChatRecyclerView()
        setupChatMessageInvalidationObserver()
        setupBackPressedDispatcherCallback()
        setupToolBar(swipedName)
        setupSendBtnListener()
        setupEmoticonBtnListener()
        setupSendChatMessageMediatorLiveDataObserver()
//        setupProfilePhoto(matchedProfilePhotoKey?.let { EndPoint.ofPhotoBucket(matchedId, it) })
        if (unmatched) setupAsUnmatched()
        setupReportMatchLiveDataObserver()
        setupUnmatchLiveDataObserver()
        setupChatMessagePagingDataObserver()
    }

    private fun setupUnmatchLiveDataObserver() {
        viewModel.unmatchLiveData.observe(viewLifecycleOwner, {
            when {
                it.isSuccess() -> popBackToMatch()
                it.isLoading() -> showLoading()
                it.isError() -> {
                    hideLoading()
                    val errorTitle = getString(R.string.error_title_report)
                    showErrorDialog(it.error, errorTitle, it.errorMessage, RequestCode.REPORT_MATCH, this)
                }
            }
        })
    }

    private fun showLoading() {
        binding.llChatLoading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.llChatLoading.visibility = View.GONE
    }

    private fun setupReportMatchLiveDataObserver() {
        viewModel.reportMatchLiveData.observe(viewLifecycleOwner, {
            when {
                it.isSuccess() -> popBackToMatch()
                it.isLoading() -> getReportDialog()?.showLoading()
                it.isError() -> {
                    getReportDialog()?.hideLoading()
                    val errorTitle = getString(R.string.error_title_report)
                    showErrorDialog(it.error, errorTitle, it.errorMessage, RequestCode.REPORT_MATCH, this)
                }
            }
        })
    }

    private fun getReportDialog(): ReportDialog? {
        return childFragmentManager.findFragmentByTag(ReportDialog.TAG)?.let { return@let it as ReportDialog }
    }

    private fun setupSendChatMessageMediatorLiveDataObserver() {
        viewModel.sendChatMessageMediatorLiveData.observe(viewLifecycleOwner, {
            val errorTitle = getString(R.string.error_title_send_chat_message)
            if (it.isError()) {
                if (it.error == ExceptionCode.MATCH_UNMATCHED_EXCEPTION) setupAsUnmatched()
                showErrorDialog(it.error, errorTitle, it.errorMessage)
            }
        })
    }

    private fun setupChatMessageInvalidationObserver() {
        viewModel.chatMessageInvalidationLiveData.observe(viewLifecycleOwner, {
            when (it.type) {
                ChatMessageInvalidation.Type.SEND -> {
                    binding.etChatMessageBody.setText("")
                    if (binding.rvChat.canScrollVertically(1)) chatMessagePagingRefreshAdapter.refresh()
                    else setupChatMessagePagingDataObserver()
                }
                ChatMessageInvalidation.Type.RECEIVED -> {
                    if (binding.rvChat.canScrollVertically(1)) it.body?.let { body ->
                        showNewChatMessageSnackBar(body)
                    } else setupChatMessagePagingDataObserver()
                }
                else -> chatMessagePagingRefreshAdapter.refresh()
            }
        })
    }

    private fun setupAsUnmatched() {
        binding.tvChatSwipedName.setTextColor(ContextCompat.getColor(requireContext(), R.color.TextGrey))
    }

    private fun setupSendBtnListener() {
        binding.btnChatMessageSend.setOnClickListener {
            viewModel.sendChatMessage(binding.etChatMessageBody.text.toString().trim())
        }
    }

    private fun setupChatMessagePagingDataObserver() {
        chatMessagePagingObserveJob?.cancel()
        registerAdapterDataObserver()
        chatMessagePagingObserveJob = lifecycleScope.launch {
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

    private fun setupToolBar(swipedName: String?) {
        binding.tvChatSwipedName.text = swipedName ?: ""
        binding.tbChat.inflateMenu(R.menu.chat_tool_bar)
        binding.tbChat.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.miChatMore -> showMoreMenu()
                else -> false
            }
        }
        binding.btnChatBack.setOnClickListener { popBackToMatch() }
    }

    private fun showMoreMenu(): Boolean {
        ChatMoreMenuDialog(this).show(childFragmentManager, ChatMoreMenuDialog.TAG)
        return true
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

    private suspend fun setupProfilePhoto(profilePhotoEndPoint: String?) = withContext(Dispatchers.IO) {
        profilePhotoEndPoint?.let { profilePhotoEndPoint ->
            runCatching {
                val file = Glide.with(requireContext()).downloadOnly().load(profilePhotoEndPoint).submit().get()
                if (file.exists()) withContext(Dispatchers.Main) {
                    chatMessagePagingAdapter.onProfilePhotoLoaded(profilePhotoEndPoint)
                }
            }.getOrNull()
        }
    }

    private fun popBackToMatch() {
        requireActivity().supportFragmentManager.popBackStack(MainViewPagerFragment.TAG, POP_BACK_STACK_INCLUSIVE)
    }

    override fun onDismissErrorDialog() {
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
        val snackBarBinding = SnackBarNewChatMessageBinding.inflate(layoutInflater)
        val snackBar = SnackBarHelper.make(binding.clChatSnackBarPlaceHolder, 0, 0, snackBarBinding.root)
        snackBarBinding.tvSnackBarNewChatMessage.text = body
        snackBarBinding.llSnackBarChatMessage.setOnClickListener {
            setupChatMessagePagingDataObserver()
            newChatMessageSnackBar?.dismiss()
        }
        snackBar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                if (transientBottomBar === newChatMessageSnackBar) newChatMessageSnackBar = null
            }
        })
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


