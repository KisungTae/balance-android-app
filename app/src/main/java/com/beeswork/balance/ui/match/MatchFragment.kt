package com.beeswork.balance.ui.match

import android.os.Bundle
import android.view.*
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentMatchBinding
import com.beeswork.balance.internal.constant.BundleKey
import com.beeswork.balance.ui.chat.ChatFragment
import com.beeswork.balance.ui.common.ScopeFragment
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.dialog.FetchErrorDialog
import com.beeswork.balance.ui.mainviewpager.MainViewPagerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class MatchFragment : ScopeFragment(),
    KodeinAware,
    MatchPagingDataAdapter.MatchListener,
    ErrorDialog.OnRetryListener {

    override val kodein by closestKodein()
    private val viewModelFactory: MatchViewModelFactory by instance()
    private lateinit var viewModel: MatchViewModel
    private lateinit var matchPagingDataAdapter: MatchPagingDataAdapter
    private lateinit var binding: FragmentMatchBinding
    private var searchJob: Job? = null
    private var scrolling = false
    private var refresh = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMatchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MatchViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = launch {
        setupMatchRecyclerView()
        setupToolBars()
        setupFetchMatchesLiveDataObserver()
        search("")
//        viewModel.testFunction()
        viewModel.fetchMatches()
    }

    private fun setupMatchRecyclerView() {
        matchPagingDataAdapter = MatchPagingDataAdapter(this@MatchFragment)
        binding.rvMatch.adapter = matchPagingDataAdapter
        binding.rvMatch.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMatch.itemAnimator = null
        binding.rvMatch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        scrolling = false
                        refreshAdapter()
                    }
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        scrolling = true
                    }
                }
            }
        })
    }

    private fun setupToolBars() {
        binding.tbMatch.inflateMenu(R.menu.match_tool_bar)
        binding.tbMatch.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.miMatchSearch -> {
                    showSearchToolBar()
//                    viewModel.testFunction()
                    true
                }
                else -> false
            }
        }
        binding.btnMatchSearchClose.setOnClickListener { hideSearchToolBar() }
        binding.etMatchSearch.addTextChangedListener { search(it.toString()) }
    }

    private fun search(keyword: String) {
        searchJob?.cancel()
        searchJob = launch {
            viewModel.initMatchPagingData(keyword.trim()).collectLatest {
                refresh = false
                matchPagingDataAdapter.submitData(it)
            }
        }
    }

    private fun hideSearchToolBar() {
        binding.tbMatchSearch.visibility = View.GONE
        binding.tbMatch.visibility = View.VISIBLE
        binding.etMatchSearch.setText("")
    }

    private fun showSearchToolBar(): Boolean {
        binding.tbMatch.visibility = View.GONE
        binding.tbMatchSearch.visibility = View.VISIBLE
        return true
    }

    private fun setupFetchMatchesLiveDataObserver() {
        viewModel.fetchMatchesLiveData.observe(viewLifecycleOwner, {
            if (it.isError()) ErrorDialog(
                it.error,
                it.errorMessage,
                this@MatchFragment,
                null
            ).show(childFragmentManager, FetchErrorDialog.TAG)
            else if (it.isSuccess()) updateRefresh()
        })
    }

    private fun updateRefresh() {
        refresh = true
        refreshAdapter()
    }

    private fun refreshAdapter() {
        if (!scrolling && refresh) matchPagingDataAdapter.refresh()
    }

    override fun onClick(position: Int) {
        val chatFragment = ChatFragment()
        val arguments = Bundle()

        matchPagingDataAdapter.getMatch(position)?.let {
            arguments.putLong(BundleKey.CHAT_ID, it.chatId)
            arguments.putString(BundleKey.MATCHED_ID, it.matchedId.toString())
            arguments.putString(BundleKey.MATCHED_NAME, it.name)
            arguments.putString(BundleKey.MATCHED_REP_PHOTO_KEY, it.repPhotoKey)
            arguments.putLong(BundleKey.LAST_READ_CHAT_MESSAGE_ID, it.lastReadChatMessageId)
            chatFragment.arguments = arguments
        }

        activity?.supportFragmentManager?.beginTransaction()?.let {
            it.add(R.id.fcvMain, chatFragment)
            it.addToBackStack(MainViewPagerFragment.TAG)
            it.commit()
        }
    }

    override fun onRetry() {
        viewModel.fetchMatches()
    }
}


