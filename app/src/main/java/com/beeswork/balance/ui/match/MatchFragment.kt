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
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.internal.Resource
import com.beeswork.balance.internal.constant.DialogTag
import com.beeswork.balance.ui.balancegame.BalanceGameDialog
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

        matchPagedListAdapter = MatchPagedListAdapter(this@MatchFragment)
        rvMatch.adapter = matchPagedListAdapter
        rvMatch.layoutManager = LinearLayoutManager(this@MatchFragment.context)

        val matches = viewModel.matches.await()

        matches.observe(viewLifecycleOwner, Observer { pagedMatchList ->
            pagedMatchList?.apply {
                matchPagedListAdapter.submitList(pagedMatchList)
            }
        })

        viewModel.fetchMatchesResponse.observe(viewLifecycleOwner, { fetchMatchesResponse ->

            when (fetchMatchesResponse.status) {
                Resource.Status.EXCEPTION -> {
                    FetchErrorDialog(
                        fetchMatchesResponse.exceptionMessage,
                        this@MatchFragment
                    ).show(childFragmentManager, DialogTag.FETCH_ERROR_DIALOG)
                }
            }
        })

        viewModel.fetchMatches()
    }

    override fun onMatchClick(view: View, chatId: Long) {
        Navigation.findNavController(view)
            .navigate(MatchFragmentDirections.matchToChatAction(chatId))
    }

    override fun onRefetch() {
        viewModel.fetchMatches()
    }
}


