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
import com.beeswork.balance.data.repository.BalanceRepository
import com.beeswork.balance.ui.base.ScopeFragment
import kotlinx.android.synthetic.main.fragment_match.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class MatchFragment: ScopeFragment(), KodeinAware, MatchRecyclerViewAdapter.OnMatchListener {

    override val kodein by closestKodein()

    private val viewModelFactory: MatchViewModelFactory by instance()
    private lateinit var viewModel: MatchViewModel
    private lateinit var matchRecyclerViewAdapter: MatchRecyclerViewAdapter


//  TODO: remove me
    private val balanceRepository: BalanceRepository by instance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_match, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MatchViewModel::class.java)
        bindUI()

        //  TODO: remove me
        addMatchBtn.setOnClickListener {
            println("click add match btn")
            balanceRepository.insertMatch()
        }

        //  TODO: remove me
        editMatchBtn.setOnClickListener {
            println("click edit match btn")
            balanceRepository.unmatch()
        }
    }

    private fun bindUI() = launch {
        matchRecyclerViewAdapter = MatchRecyclerViewAdapter(this@MatchFragment)

        rvMatch.adapter = matchRecyclerViewAdapter
        rvMatch.layoutManager = LinearLayoutManager(this@MatchFragment.context)

        val matches = viewModel.matches.await()

        matches.observe(viewLifecycleOwner, Observer { currentMatches ->
            if (currentMatches == null) return@Observer
            matchRecyclerViewAdapter.setMatches(currentMatches)
        })

    }

    override fun onMatchClick(view: View, matchId: Int) {
        Navigation.findNavController(view).navigate(MatchFragmentDirections.matchToChatAction(matchId))
    }
}


