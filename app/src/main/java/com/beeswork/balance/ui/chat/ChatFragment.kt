package com.beeswork.balance.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentChatBinding
import com.beeswork.balance.internal.constant.BundleKey
import com.beeswork.balance.internal.constant.EndPoint
import com.beeswork.balance.internal.util.safeLet
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
        } ?: showErrorDialog()
    }

    private fun bindUI(matchedId: UUID, matchedName: String?, matchedRepPhotoKey: String?) = launch {
        setupBackPressedDispatcherCallback()
        setupToolBar(matchedName)
        setupSendBtnListener()
        setupEmoticonBtnListener()
        setupChatRecyclerView(matchedId, matchedRepPhotoKey)
        if (matchedRepPhotoKey == null) setupAsUnmatched()
        setupChatMessagePagingData()
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

    private fun setupChatRecyclerView(matchedId: UUID, repPhotoKey: String?) {
        chatMessagePagingAdapter = ChatMessagePagingAdapter(
            repPhotoKey?.let { EndPoint.ofPhotoBucket(matchedId, repPhotoKey) },
            requireContext()
        )
        binding.rvChat.adapter = chatMessagePagingAdapter
        val layoutManager = LinearLayoutManager(this@ChatFragment.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        layoutManager.reverseLayout = true
        binding.rvChat.layoutManager = layoutManager
    }

    private fun showErrorDialog() {
        ErrorDialog(
            null,
            getString(R.string.chat_id_not_found_exception),
            null,
            this
        ).show(childFragmentManager, ErrorDialog.TAG)
    }

    private fun popBackToMatch() {
        requireActivity().supportFragmentManager.popBackStack(MainViewPagerFragment.TAG, POP_BACK_STACK_INCLUSIVE)
    }

    override fun onDismiss() {
        popBackToMatch()
    }
}


