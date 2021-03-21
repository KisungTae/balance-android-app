package com.beeswork.balance.ui.match

import android.os.Bundle
import android.view.*
import android.widget.AbsListView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentMatchBinding
import com.beeswork.balance.internal.constant.LoadType
import com.beeswork.balance.ui.chat.ChatFragment
import com.beeswork.balance.ui.common.ScopeFragment
import com.beeswork.balance.ui.dialog.FetchErrorDialog
import com.beeswork.balance.ui.mainviewpager.MainViewPagerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import org.kodein.di.newInstance

class MatchFragment : ScopeFragment(), KodeinAware, MatchPagedListAdapter.OnClickMatchListener,
    FetchErrorDialog.FetchErrorListener {

    override val kodein by closestKodein()
    private val viewModelFactory: MatchViewModelFactory by instance()
    private lateinit var viewModel: MatchViewModel
    private lateinit var matchPagedListAdapter: MatchPagedListAdapter
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
        setupFetchMatchesLiveDataObserver()
        setupMatchPagedListLiveDataObserver()
        setupToolBars()
//        viewModel.fetchMatches()
    }

    private fun setupToolBars() {


        binding.tbMatch.inflateMenu(R.menu.match_tool_bar)
        binding.tbMatch.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.miMatchSearch -> {

//                    println("matchPagedListAdapter.currentList?.positionOffset: ${matchPagedListAdapter.currentList?.positionOffset}")
//                    println("matchPagedListAdapter.currentList?.size: ${matchPagedListAdapter.currentList?.size}")
                    viewModel.testFunction()
                    showSearchToolBar()
//                    matchPagedListAdapter.refresh()

                    true
                }
                else -> false
            }
        }



        binding.btnMatchSearchClose.setOnClickListener {
//            viewModel.testFunction()
            matchPagedListAdapter.refresh()
//            hideSearchToolBar()
        }
        binding.etMatchSearch.addTextChangedListener {
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                viewModel.initializeMatches("%${it.toString()}%").collectLatest {
                    matchPagedListAdapter.submitData(it)
                }
            }
//            viewModel.changeMatchSearchKeyword(it.toString())
        }
    }

    private fun hideSearchToolBar() {
        binding.tbMatchSearch.visibility = View.GONE
        binding.tbMatch.visibility = View.VISIBLE
//        viewModel.changeMatchSearchKeyword("")
    }

    private fun showSearchToolBar() {
        binding.tbMatch.visibility = View.GONE
        binding.tbMatchSearch.visibility = View.VISIBLE
    }

    private fun setupFetchMatchesLiveDataObserver() {
        lifecycleScope.launch {
            viewModel.initializeMatches("%%").collectLatest {
                matchPagedListAdapter.submitData(it)
            }
        }
//        viewModel.fetchMatchesLiveData.observe(viewLifecycleOwner, {
//            if (it.isError()) FetchErrorDialog(it.errorMessage, this@MatchFragment).show(
//                childFragmentManager,
//                FetchErrorDialog.TAG
//            )
//        })
    }

    private suspend fun setupMatchPagedListLiveDataObserver() {
//        viewModel.matchPagedListLiveData.await().observe(viewLifecycleOwner, {
//            matchPagedListAdapter.submitList(it)
//        })
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewModel.matches.collectLatest {
//                matchPagedListAdapter.submitData(it)
//            }
//        }
    }

    private fun setupMatchRecyclerView() {
        matchPagedListAdapter = MatchPagedListAdapter(this@MatchFragment)
        binding.rvMatch.adapter = matchPagedListAdapter
        binding.rvMatch.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMatch.itemAnimator = null
    }

    override fun onClick(view: View) {
        val chatFragment = ChatFragment()
        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.add(R.id.fcvMain, chatFragment)
        fragmentTransaction?.addToBackStack(MainViewPagerFragment.TAG)
        fragmentTransaction?.commit()
    }

    override fun onRefetch() {
        viewModel.fetchMatches()
    }
}


