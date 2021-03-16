package com.beeswork.balance.ui.match

import android.os.Bundle
import android.view.*
import android.widget.AbsListView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentMatchBinding
import com.beeswork.balance.internal.constant.LoadType
import com.beeswork.balance.ui.chat.ChatFragment
import com.beeswork.balance.ui.common.ScopeFragment
import com.beeswork.balance.ui.dialog.FetchErrorDialog
import com.beeswork.balance.ui.mainviewpager.MainViewPagerFragment
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class MatchFragment : ScopeFragment(), KodeinAware, MatchPagedListAdapter.OnClickMatchListener,
    FetchErrorDialog.FetchErrorListener {

    override val kodein by closestKodein()
    private val viewModelFactory: MatchViewModelFactory by instance()
    private lateinit var viewModel: MatchViewModel
    private lateinit var matchPagedListAdapter: MatchPagedListAdapter
    private lateinit var matchRecyclerViewAdapter: MatchRecyclerViewAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var binding: FragmentMatchBinding

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
//                    showSearchToolBar()
//                    TODO: remove me
                    viewModel.loadMoreMatches(0, 0, LoadType.APPEND)
                    true
                }
                else -> false
            }
        }

        binding.btnMatchSearchClose.setOnClickListener { hideSearchToolBar() }
        binding.etMatchSearch.addTextChangedListener {
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
        viewModel.fetchMatchesLiveData.observe(viewLifecycleOwner, {
//            if (it.isError()) FetchErrorDialog(it.errorMessage, this@MatchFragment).show(
//                childFragmentManager,
//                FetchErrorDialog.TAG
//            )
        })
    }

    private suspend fun setupMatchPagedListLiveDataObserver() {
//        viewModel.matchPagedListLiveData.await().observe(viewLifecycleOwner, {
//            matchPagedListAdapter.submitList(it)
//        })

////      TODO: remove me
//        viewModel.matches.await().observe(viewLifecycleOwner, {
//            matchPagedListAdapter.submitList(it)
//        })
    }

    private var isLoading: Boolean = false
    private var isScrolling: Boolean = false

    private fun setupMatchRecyclerView() {
//        matchRecyclerViewAdapter = MatchRecyclerViewAdapter(this@MatchFragment)
        matchPagedListAdapter = MatchPagedListAdapter(this@MatchFragment)
        linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvMatch.adapter = matchPagedListAdapter
        binding.rvMatch.layoutManager = linearLayoutManager
        binding.rvMatch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    isScrolling = true
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val currentItems = linearLayoutManager.childCount
                val totalItems = linearLayoutManager.itemCount
                val scrollOutItems = linearLayoutManager.findFirstVisibleItemPosition()

                if (isScrolling && ((currentItems + scrollOutItems) == totalItems))
                    loadMoreMatches()
            }
        })
    }
    
    private fun loadMoreMatches() {
        println("loadMoreMatches")
    }



//    private fun setupMatchRecyclerView() {
//        matchPagedListAdapter = MatchPagedListAdapter(this@MatchFragment)
//        binding.rvMatch.adapter = matchPagedListAdapter
//        binding.rvMatch.layoutManager = LinearLayoutManager(requireContext())
//    }

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


