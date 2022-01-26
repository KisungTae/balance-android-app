package com.beeswork.balance.ui.chat

import android.os.Bundle
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
import com.beeswork.balance.data.database.repository.chat.ChatMessageInvalidation
import com.beeswork.balance.databinding.FragmentChatBinding
import com.beeswork.balance.databinding.SnackBarNewChatMessageBinding
import com.beeswork.balance.internal.constant.*
import com.beeswork.balance.internal.util.*
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.common.PagingRefreshAdapter
import com.beeswork.balance.ui.dialog.ConfirmDialog
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.dialog.ReportDialog
import com.beeswork.balance.ui.mainviewpager.MainViewPagerFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.factory
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
        } ?: kotlin.run {
            val title = getString(R.string.error_title_open_chat)
            val message = getString(R.string.error_title_chat_id_not_found)
            ErrorDialog.show(title, message, this, childFragmentManager)
        }
    }

    private fun bindUI(
        swipedId: UUID,
        swipedName: String?,
        swipedProfilePhotoKey: String?,
        unmatched: Boolean
    ) = lifecycleScope.launch {
        setupChatRecyclerView()
        observeChatMessageInvalidation()
        setupBackPressedDispatcherCallback()
        setupToolBar(swipedName)
        setupSendBtnListener()
        setupEmoticonBtnListener()
        observeSendChatMessageMediatorLiveData()
        setupProfilePhoto(swipedId, swipedProfilePhotoKey)
        if (unmatched) {
            setupAsUnmatched()
        }
        observeReportMatchLiveData()
        observeUnmatchLiveData()
        observeChatMessagePagingData()

//        todo: remove me
//        viewModel.test()
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
                if (resource.isExceptionEqualTo(ExceptionCode.MATCH_UNMATCHED_EXCEPTION)) {
                    setupAsUnmatched()
                }
                showSendChatMessageErrorDialog(resource.exception)
            }
        }
    }

    private fun showSendChatMessageErrorDialog(exception: Throwable?) {
        val title = getString(R.string.error_title_send_chat_message)
        val message = MessageSource.getMessage(requireContext(), exception)
        ErrorDialog.show(title, message, childFragmentManager)
    }

    private suspend fun observeChatMessageInvalidation() {
        viewModel.chatMessageInvalidationLiveData.await().observe(viewLifecycleOwner, { resource ->
            when (resource.type) {
                ChatMessageInvalidation.Type.SEND -> {
                    binding.etChatMessageBody.setText("")
                    if (binding.rvChat.canScrollVertically(1)) {
                        chatMessagePagingRefreshAdapter.refresh()
                    } else {
                        observeChatMessagePagingData()
                    }
                }
                ChatMessageInvalidation.Type.RECEIVED -> {
                    if (binding.rvChat.canScrollVertically(1)) {
                        resource.body?.let { body ->
                            showNewChatMessageSnackBar(body)
                        }
                    } else {
                        observeChatMessagePagingData()
                    }
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
            binding.etChatMessageBody.requestFocus()
//            val inputManager = (activity?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager)
//            inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0)
        }
    }

    private fun observeChatMessagePagingData() {
        chatMessagePagingObserveJob?.cancel()
        registerAdapterDataObserver()
        chatMessagePagingObserveJob = lifecycleScope.launch {
            viewModel.initChatMessagePagingData().observe(viewLifecycleOwner) {
                chatMessagePagingRefreshAdapter.reset()
                lifecycleScope.launch {
                    chatMessagePagingAdapter.submitData(it)
                }
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
                popBackStack(MainViewPagerFragment.TAG)
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
        binding.btnChatBack.setOnClickListener { popBackStack(MainViewPagerFragment.TAG) }
    }

    private fun showMoreMenu(): Boolean {
        ChatMoreMenuDialog(this).show(childFragmentManager, ChatMoreMenuDialog.TAG)
        return true
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

    private fun setupEmoticonBtnListener() {
        binding.btnChatEmoticon.setOnClickListener {
//            val view = activity?.currentFocus
//            val methodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            methodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private suspend fun setupProfilePhoto(accountId: UUID, photoKey: String?) = withContext(Dispatchers.IO) {
//        EndPoint.ofPhoto(accountId, photoKey)?.let { profilePhotoEndPoint ->
//            runCatching {
//                val file = Glide.with(requireContext()).downloadOnly().load(profilePhotoEndPoint).submit().get()
//                if (file.exists()) withContext(Dispatchers.Main) {
//                    chatMessagePagingAdapter.onProfilePhotoLoaded(profilePhotoEndPoint)
//                }
//            }.getOrNull()
//        }
    }

    override fun onDismissErrorDialog(id: UUID?) {
        popBackStack(MainViewPagerFragment.TAG)
    }

    override fun onResendChatMessage(position: Int) {
        chatMessagePagingAdapter.getChatMessage(position)?.let { chatMessageDomain ->
            viewModel.resendChatMessage(chatMessageDomain.id)
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
            observeChatMessagePagingData()
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


