package com.beeswork.balance.ui.match

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.databinding.FragmentMatchBinding
import com.beeswork.balance.ui.common.ScopeFragment
import com.beeswork.balance.ui.common.SwipeButton
import com.beeswork.balance.ui.common.SwipeButtonClickListener
import com.beeswork.balance.ui.common.SwipeButtonHelper
import com.beeswork.balance.ui.dialog.FetchErrorDialog
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
        setupFetchMatchesObserver()
        setupMatchesObserver()
        viewModel.fetchMatches()
    }

    private fun setupFetchMatchesObserver() {
        viewModel.fetchMatches.observe(viewLifecycleOwner, {
//            if (it.isError()) FetchErrorDialog(it.errorMessage, this@MatchFragment).show(
//                childFragmentManager,
//                FetchErrorDialog.TAG
//            )
        })
    }

    private suspend fun setupMatchesObserver() {
        viewModel.matches.await().observe(viewLifecycleOwner, {
            matchPagedListAdapter.submitList(it)
        })
    }

    private fun setupMatchRecyclerView() {
        val swipe = object : SwipeButtonHelper(requireContext(), binding.rvMatch, 200) {
            override fun initiateSwipeButton(viewHolder: RecyclerView.ViewHolder, buffer: MutableList<SwipeButton>) {
                buffer.add(
                    SwipeButton(
                        requireContext(),
                        "delete",
                        10,
                        Color.parseColor("#000000"),
                        Color.parseColor("#000000"),
                        object : SwipeButtonClickListener {
                            override fun onClick(pos: Int) {
                                println("onclick: $pos")
                            }

                        }
                    )
                )
            }

        }

        matchPagedListAdapter = MatchPagedListAdapter()
        binding.rvMatch.adapter = matchPagedListAdapter
        binding.rvMatch.layoutManager = LinearLayoutManager(requireContext())
    }


    override fun onMatchClick(view: View, position: Int) {
//      TODO: it.matchedId to UUID
//        matchPagedListAdapter.getMatchByPosition(position)?.let {
//            Navigation.findNavController(view)
//                .navigate(MatchFragmentDirections.matchToChatAction(it.chatId, "it.matchedId"))
//        }
    }

    override fun onRefetch() {
        viewModel.fetchMatches()
    }
}


