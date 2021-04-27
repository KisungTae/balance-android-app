package com.beeswork.balance.ui.match

import android.os.Bundle
import android.view.*
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentMatchBinding
import com.beeswork.balance.internal.constant.BundleKey
import com.beeswork.balance.internal.constant.RequestCode
import com.beeswork.balance.ui.chat.ChatFragment
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.common.PagingRefreshAdapter
import com.beeswork.balance.ui.common.ViewPagerChildFragment
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.dialog.FetchErrorDialog
import com.beeswork.balance.ui.dialog.NewMatchDialog
import com.beeswork.balance.ui.mainviewpager.MainViewPagerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class MatchFragment : BaseFragment(), KodeinAware, MatchPagingDataAdapter.MatchListener,
    ErrorDialog.OnRetryListener, ViewPagerChildFragment {

    override val kodein by closestKodein()
    private val viewModelFactory: MatchViewModelFactory by instance()
    private lateinit var viewModel: MatchViewModel
    private lateinit var matchPagingDataAdapter: MatchPagingDataAdapter
    private lateinit var matchPagingRefreshAdapter: PagingRefreshAdapter<MatchDomain, MatchPagingDataAdapter.ViewHolder>
    private lateinit var binding: FragmentMatchBinding
    private var searchJob: Job? = null

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

    private fun bindUI() = lifecycleScope.launch {
        setupMatchRecyclerView()
        setupToolBars()
        setupFetchMatchesLiveDataObserver()
        setupMatchPagingRefreshLiveData()
        search("")
        viewModel.fetchMatches()
    }

    private fun setupMatchPagingRefreshLiveData() {
        viewModel.matchPagingRefreshLiveData.observe(viewLifecycleOwner, { pagingRefresh ->
            pagingRefresh.newMatch?.let { newMatch ->
                NewMatchDialog(
                    newMatch.matchedId,
                    newMatch.matchedName,
                    newMatch.matchedRepPhotoKey,
                    newMatch.accountId,
                    newMatch.repPhotoKey
                ).show(childFragmentManager, NewMatchDialog.TAG)
            }
            matchPagingRefreshAdapter.refresh()
        })
    }

    private fun setupMatchRecyclerView() {
        matchPagingDataAdapter = MatchPagingDataAdapter(this@MatchFragment)
        binding.rvMatch.adapter = matchPagingDataAdapter
        binding.rvMatch.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMatch.itemAnimator = null
        matchPagingRefreshAdapter = PagingRefreshAdapter(binding.rvMatch, matchPagingDataAdapter)
    }

    private fun setupToolBars() {
        binding.tbMatch.inflateMenu(R.menu.match_tool_bar)
        binding.tbMatch.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.miMatchSearch -> {
                    viewModel.testFunction()
                    showSearchToolBar()
                }
                else -> false
            }
        }
        binding.btnMatchSearchClose.setOnClickListener { hideSearchToolBar() }
        binding.etMatchSearch.addTextChangedListener { search(it.toString()) }
    }

    private fun search(keyword: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.initMatchPagingData(keyword.trim()).observe(viewLifecycleOwner, {
                matchPagingRefreshAdapter.reset()
                lifecycleScope.launch { matchPagingDataAdapter.submitData(it) }
            })
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
            if (it.isError() && validateAccount(it.error, it.errorMessage)) ErrorDialog(
                it.error,
                errorTitle(),
                it.errorMessage,
                RequestCode.FETCH_MATCHES,
                this@MatchFragment,
                null
            ).show(childFragmentManager, FetchErrorDialog.TAG)
        })
    }

    private fun errorTitle(): String {
        val currentFragment = activity?.supportFragmentManager?.fragments?.lastOrNull()?.javaClass
        val resourceId = if (currentFragment == ChatFragment::class.java) R.string.error_title_fetch_chat_messages
        else R.string.error_title_fetch_matches
        return getString(resourceId)
    }

    override fun onClick(position: Int) {
        val chatFragment = ChatFragment()
        val arguments = Bundle()

        matchPagingDataAdapter.getMatch(position)?.let {
            arguments.putLong(BundleKey.CHAT_ID, it.chatId)
            arguments.putString(BundleKey.MATCHED_ID, it.matchedId.toString())
            arguments.putString(BundleKey.MATCHED_NAME, it.name)
            arguments.putBoolean(BundleKey.UNMATCHED, it.unmatched)
            arguments.putString(BundleKey.MATCHED_REP_PHOTO_KEY, it.repPhotoKey)
            chatFragment.arguments = arguments
        }

        activity?.supportFragmentManager?.beginTransaction()?.let {
            it.addToBackStack(MainViewPagerFragment.TAG)
            it.add(R.id.fcvMain, chatFragment)
            it.commit()
        }
    }

    override fun onRetry(requestCode: Int?) {
        requestCode?.let {
            when (it) {
                RequestCode.FETCH_MATCHES -> viewModel.fetchMatches()
            }
        }

    }

    override fun onFragmentSelected() {
        viewModel.fetchMatches()
    }
}


