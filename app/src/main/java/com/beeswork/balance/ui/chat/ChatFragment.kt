package com.beeswork.balance.ui.chat

import android.database.DataSetObserver
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.databinding.FragmentChatBinding
import com.beeswork.balance.internal.constant.BundleKey
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.constant.EndPoint
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.util.safeLet
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.common.ScopeFragment
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.dialog.FetchErrorDialog
import com.beeswork.balance.ui.mainviewpager.MainViewPagerFragment
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.factory
import java.time.OffsetDateTime
import java.util.*
import kotlin.random.Random


class ChatFragment : BaseFragment(),
    KodeinAware,
    ErrorDialog.OnDismissListener {

    override val kodein by closestKodein()
    private val viewModelFactory: ((ChatViewModelFactoryParameter) -> ChatViewModelFactory) by factory()
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
                arguments.getString(BundleKey.MATCHED_REP_PHOTO_KEY)
            )
        } ?: ErrorDialog(null, getString(R.string.error_title_chat_id_not_found), "", null, this).show(
            childFragmentManager,
            ErrorDialog.TAG
        )
    }

    private fun bindUI(matchedId: UUID, matchedName: String?, matchedRepPhotoKey: String?) = lifecycleScope.launch {
        setupBackPressedDispatcherCallback()
        setupToolBar(matchedName)
        setupSendBtnListener()
        setupEmoticonBtnListener()
        setupChatRecyclerView()
//        setupRepPhoto(matchedRepPhotoKey?.let { EndPoint.ofPhotoBucket(matchedId, it) })
        if (matchedRepPhotoKey == null) setupAsUnmatched()
        setupChatMessagePagingData()
    }


    private fun updateRefresh() {

    }


    private fun setupAsUnmatched() {
        binding.tvChatMatchedName.setTextColor(ContextCompat.getColor(requireContext(), R.color.TextGrey))
        binding.llChatInputWrapper.visibility = View.GONE
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
        viewModel.initChatMessagePagingData().collectLatest {
            chatMessagePagingAdapter.submitData(it)
        }
    }

    private fun registerAdapterDataObserver() {
        chatMessagePagingAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
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
//                    println("current fragment: ${activity?.supportFragmentManager?.fragments?.firstOrNull()}")
//                    println("current fragment: ${activity?.supportFragmentManager?.fragments?.lastOrNull()}")
//                    chatMessagePagingAdapter.refresh()
//                    viewModel.test()
                    validateAccount(ExceptionCode.ACCOUNT_BLOCKED_EXCEPTION, "")
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


