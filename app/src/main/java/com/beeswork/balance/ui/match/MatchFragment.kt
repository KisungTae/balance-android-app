package com.beeswork.balance.ui.match

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.beeswork.balance.R
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.ui.base.ScopeFragment
import com.beeswork.balance.ui.dialog.FetchErrorDialog
import kotlinx.android.synthetic.main.fragment_match.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class MatchFragment : ScopeFragment(), KodeinAware, MatchPagedListAdapter.OnMatchListener,
    FetchErrorDialog.FetchErrorListener {

    override val kodein by closestKodein()

    private val viewModelFactory: MatchViewModelFactory by instance()
    private lateinit var viewModel: MatchViewModel
    private lateinit var matchPagedListAdapter: MatchPagedListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_match, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MatchViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = launch {
        setupMatchRecyclerView()
        setupFetchMatchesObserver()
        setupMatchesObserver()
        viewModel.fetchMatches()

//      TODO: remove me
        changeButton.setOnClickListener {
            viewModel.change()
        }
    }

    private fun setupFetchMatchesObserver() {
        viewModel.fetchMatches.observe(viewLifecycleOwner, {
            when (it.status) {
                Resource.Status.ERROR -> {
                    FetchErrorDialog(
                        it.errorMessage,
                        this@MatchFragment
                    ).show(childFragmentManager, FetchErrorDialog.TAG)
                }
            }
        })
    }

    private suspend fun setupMatchesObserver() {
        viewModel.matches.await().observe(viewLifecycleOwner, {
//            matchPagedListAdapter.submitList(it)
        })
    }

    private fun setupMatchRecyclerView() {
        matchPagedListAdapter = MatchPagedListAdapter(this@MatchFragment)
        rvMatch.adapter = matchPagedListAdapter
        rvMatch.layoutManager = LinearLayoutManager(this@MatchFragment.context)
    }



    override fun onMatchClick(view: View, position: Int) {
//      TODO: it.matchedId to UUID
        matchPagedListAdapter.getMatchByPosition(position)?.let {
            Navigation.findNavController(view)
                .navigate(MatchFragmentDirections.matchToChatAction(it.chatId, "it.matchedId"))
        }
    }

    override fun onRefetch() {
        viewModel.fetchMatches()
    }
}


