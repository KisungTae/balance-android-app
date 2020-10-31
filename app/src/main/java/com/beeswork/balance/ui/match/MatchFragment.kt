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
import com.beeswork.balance.ui.base.ScopeFragment
import kotlinx.android.synthetic.main.fragment_match.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class MatchFragment : ScopeFragment(), KodeinAware, MatchPagedListAdapter.OnMatchListener {

    override val kodein by closestKodein()

    private val viewModelFactory: MatchViewModelFactory by instance()
    private lateinit var viewModel: MatchViewModel
    private lateinit var matchPagedListAdapter: MatchPagedListAdapter


    //  TODO: remove me
    private val balanceRepository: BalanceRepository by instance()

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

        //  TODO: remove me
        editMatchBtn.setOnClickListener {
            println("click edit match btn")
//            balanceRepository.unmatch()
            viewModel.fetchMatches()
        }
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

        viewModel.fetchMatches()

        viewModel.fetchMatchesResponse.observe(viewLifecycleOwner, { fetchMatchResource ->

            when (fetchMatchResource.status) {
                Resource.Status.SUCCESS -> {
                    println("fetchMatch success")
                }

                Resource.Status.EXCEPTION -> {
                    println("fetchMatch exception")
                }

                Resource.Status.LOADING -> {
                    println("fetchMatch loading")
                }
            }
        })
    }

    override fun onMatchClick(view: View, chatId: Long) {
        Navigation.findNavController(view).navigate(MatchFragmentDirections.matchToChatAction(chatId))
    }
}


