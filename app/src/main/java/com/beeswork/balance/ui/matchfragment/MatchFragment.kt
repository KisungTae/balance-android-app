package com.beeswork.balance.ui.matchfragment

import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentMatchBinding
import com.beeswork.balance.internal.constant.BundleKey
import com.beeswork.balance.internal.constant.MatchPageFilter
import com.beeswork.balance.internal.util.Navigator
import com.beeswork.balance.ui.chatfragment.ChatFragment
import com.beeswork.balance.ui.common.*
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.mainviewpagerfragment.MainViewPagerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.util.*

class MatchFragment : BaseFragment(), KodeinAware, MatchPagingDataAdapter.MatchListener,
    ErrorDialog.RetryListener, ViewPagerChildFragment {

    override val kodein by closestKodein()
    private val viewModelFactory: MatchViewModelFactory by instance()
    private lateinit var viewModel: MatchViewModel
    private lateinit var matchPagingDataAdapter: MatchPagingDataAdapter
    private lateinit var matchPagingRefreshAdapter: PagingRefreshAdapter<MatchItemUIState, MatchPagingDataAdapter.ViewHolder>
    private lateinit var matchPagingInitialPageAdapter: PagingInitialPageAdapter<MatchItemUIState, MatchPagingDataAdapter.ViewHolder>
    private lateinit var binding: FragmentMatchBinding
    private var matchPageFilterJob: Job? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMatchBinding.inflate(inflater)
        return binding.root
    }

    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MatchViewModel::class.java)
        bindUI()
    }

    @ExperimentalPagingApi
    private fun bindUI() = lifecycleScope.launch {
        setupToolBars()
        setupMatchRecyclerView()
        setupMatchPagingInitialPageAdapter()
        observeMatchPageInvalidationLiveData()
        observeMatchPagingDataLiveData(null)
    }

    private suspend fun observeMatchPageInvalidationLiveData() {
        viewModel.matchPageInvalidationLiveData.await().observe(viewLifecycleOwner) {
            matchPagingRefreshAdapter.refresh()
        }
    }

    private fun setupMatchRecyclerView() {
        matchPagingDataAdapter = MatchPagingDataAdapter(this@MatchFragment)
//        footerLoadStateAdapter = BalanceLoadStateAdapter(matchPagingDataAdapter::retry)
//        binding.rvMatch.adapter = matchPagingDataAdapter.withLoadStateFooter(
//            footer = footerLoadStateAdapter
//        )
        binding.rvMatch.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMatch.itemAnimator = null
        matchPagingRefreshAdapter = PagingRefreshAdapter(binding.rvMatch, matchPagingDataAdapter)
    }

    @ExperimentalPagingApi
    private fun setupToolBars() {
        binding.tbMatch.inflateMenu(R.menu.match_tool_bar)
        binding.tbMatch.setOnMenuItemClickListener { menuItem ->
            if (menuItem === binding.tbMatch.menu.findItem(R.id.miMatchFilter)) {
                return@setOnMenuItemClickListener true
            }
            highlightSelectedMenuItem(menuItem)
            when (menuItem.itemId) {
                R.id.miMatchFilterByAll -> {
                    observeMatchPagingDataLiveData(null)
                    true
                }
                R.id.miMatchFilterByMatch -> {
                    observeMatchPagingDataLiveData(MatchPageFilter.MATCH)
                    true
                }
                R.id.miMatchFilterByChat -> {
                    observeMatchPagingDataLiveData(MatchPageFilter.CHAT)
                    true
                }
                R.id.miMatchFilterByChatWithMessages -> {
                    observeMatchPagingDataLiveData(MatchPageFilter.CHAT_WITH_MESSAGES)
                    true
                }
                else -> false
            }
        }
        highlightSelectedMenuItem(binding.tbMatch.menu.findItem(R.id.miMatchFilter).subMenu.findItem(R.id.miMatchFilterByAll))
    }

    private fun highlightSelectedMenuItem(selectedMenuItem: MenuItem) {
        binding.tbMatch.menu.findItem(R.id.miMatchFilter).subMenu.forEach { menuItem ->
            val color = if (menuItem === selectedMenuItem) {
                ContextCompat.getColor(requireContext(), R.color.Primary)
            } else {
                ContextCompat.getColor(requireContext(), R.color.TextBlack)
            }
            val span = SpannableString(menuItem.title)
            span.setSpan(ForegroundColorSpan(color), 0, span.length, 0)
            menuItem.title = span
        }
    }

    @ExperimentalPagingApi
    private fun observeMatchPagingDataLiveData(matchPageFilter: MatchPageFilter?) {
        matchPageFilterJob?.cancel()
        matchPageFilterJob = lifecycleScope.launch {
            viewModel.initMatchPagingData(matchPageFilter).observe(viewLifecycleOwner) { pagingData ->
                matchPagingRefreshAdapter.reset()
                lifecycleScope.launch {
                    matchPagingDataAdapter.submitData(pagingData)
                    binding.rvMatch.scrollToPosition(0)
                }
            }
        }
    }

    override fun onClick(position: Int) {
        val chatFragment = ChatFragment()
        val arguments = Bundle()

        matchPagingDataAdapter.getMatch(position)?.let { matchItemUIState ->
            arguments.putString(BundleKey.CHAT_ID, matchItemUIState.chatId.toString())
            arguments.putString(BundleKey.SWIPED_ID, matchItemUIState.swipedId.toString())
            chatFragment.arguments = arguments
        }
        Navigator.moveToFragment(activity, chatFragment, R.id.fcvMain, MainViewPagerFragment.TAG)
    }

    private fun setupMatchPagingInitialPageAdapter() {
        binding.btnMatchRetry.setOnClickListener {
            matchPagingDataAdapter.retry()
        }
        matchPagingInitialPageAdapter = PagingInitialPageAdapter(
            matchPagingDataAdapter,
            binding.llMatchInitialLoadingPage,
            binding.llMatchInitialErrorPage,
            binding.llMatchInitialEmptyPage,
            binding.tvMatchErrorMessage,
            requireContext()
        )
        lifecycleScope.launch {
            matchPagingDataAdapter.loadStateFlow.collect { loadState ->
                matchPagingInitialPageAdapter.updateUI(loadState)
            }
        }
    }

    override fun onRetry(requestCode: Int?) {
    }

    override fun onFragmentSelected() {
//        viewModel.test()
    }
}


