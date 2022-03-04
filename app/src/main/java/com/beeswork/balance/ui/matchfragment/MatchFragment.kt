package com.beeswork.balance.ui.matchfragment

import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.databinding.FragmentMatchBinding
import com.beeswork.balance.internal.constant.BundleKey
import com.beeswork.balance.internal.constant.MatchPageFilter
import com.beeswork.balance.internal.constant.RequestCode
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.internal.util.MessageSource
import com.beeswork.balance.ui.chatfragment.ChatFragment
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.common.PagingRefreshAdapter
import com.beeswork.balance.ui.common.ViewPagerChildFragment
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.mainviewpagerfragment.MainViewPagerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import org.threeten.bp.OffsetDateTime
import java.util.*

class MatchFragment : BaseFragment(), KodeinAware, MatchPagingDataAdapter.MatchListener,
    ErrorDialog.RetryListener, ViewPagerChildFragment {

    override val kodein by closestKodein()
    private val viewModelFactory: MatchViewModelFactory by instance()
    private lateinit var viewModel: MatchViewModel
    private lateinit var matchPagingDataAdapter: MatchPagingDataAdapter
    private lateinit var matchPagingRefreshAdapter: PagingRefreshAdapter<MatchDomain, MatchPagingDataAdapter.ViewHolder>
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
        setupMatchRecyclerView()
        setupToolBars()
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
        binding.rvMatch.adapter = matchPagingDataAdapter
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

        matchPagingDataAdapter.getMatch(position)?.let { matchDomain ->
            arguments.putString(BundleKey.CHAT_ID, matchDomain.chatId.toString())
            arguments.putString(BundleKey.SWIPED_ID, matchDomain.swipedId.toString())
            arguments.putString(BundleKey.SWIPED_NAME, matchDomain.swipedName)
            arguments.putString(BundleKey.SWIPED_PROFILE_PHOTO_KEY, matchDomain.swipedProfilePhotoKey)
            chatFragment.arguments = arguments
        }
        moveToFragment(chatFragment, R.id.fcvMain, MainViewPagerFragment.TAG)
    }

    override fun onRetry(requestCode: Int?) {
    }

    override fun onFragmentSelected() {
    }
}

