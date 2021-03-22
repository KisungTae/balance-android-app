package com.beeswork.balance.ui.match

import android.os.Bundle
import android.view.*
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentMatchBinding
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

class MatchFragment : ScopeFragment(), KodeinAware, MatchPagingDataAdapter.OnClickMatchListener,
    FetchErrorDialog.OnRetryListener {

    override val kodein by closestKodein()
    private val viewModelFactory: MatchViewModelFactory by instance()
    private lateinit var viewModel: MatchViewModel
    private lateinit var matchPagingDataAdapter: MatchPagingDataAdapter
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

    private fun bindUI() = launch {
        setupMatchRecyclerView()
        setupToolBars()
        setupFetchMatchesLiveDataObserver()
        search("")
        viewModel.fetchMatches()
    }

    private fun setupMatchRecyclerView() {
        matchPagingDataAdapter = MatchPagingDataAdapter(this@MatchFragment)
        binding.rvMatch.adapter = matchPagingDataAdapter
        binding.rvMatch.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMatch.itemAnimator = null
    }

    private fun setupToolBars() {
        binding.tbMatch.inflateMenu(R.menu.match_tool_bar)
        binding.tbMatch.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.miMatchSearch -> {
                    showSearchToolBar()
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
            viewModel.initializeMatchPagingData(keyword.trim()).collectLatest {
                matchPagingDataAdapter.submitData(it)
            }
        }
    }

    private fun hideSearchToolBar() {
        binding.tbMatchSearch.visibility = View.GONE
        binding.tbMatch.visibility = View.VISIBLE
        search("")
    }

    private fun showSearchToolBar() {
        binding.tbMatch.visibility = View.GONE
        binding.tbMatchSearch.visibility = View.VISIBLE
    }

    private fun setupFetchMatchesLiveDataObserver() {
        viewModel.fetchMatchesLiveData.observe(viewLifecycleOwner, {
            if (it.isError()) ErrorDialog(it.error, it.errorMessage, this@MatchFragment).show(
                childFragmentManager,
                FetchErrorDialog.TAG
            )
        })
    }

    override fun onClick(view: View) {
        val chatFragment = ChatFragment()
        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.add(R.id.fcvMain, chatFragment)
        fragmentTransaction?.addToBackStack(MainViewPagerFragment.TAG)
        fragmentTransaction?.commit()
    }

    override fun onRetry() {
        viewModel.fetchMatches()
    }
}


