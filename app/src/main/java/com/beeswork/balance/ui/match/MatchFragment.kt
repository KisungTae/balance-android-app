package com.beeswork.balance.ui.match

import android.os.Bundle
import android.view.*
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentMatchBinding
import com.beeswork.balance.ui.common.ScopeFragment
import com.beeswork.balance.ui.dialog.FetchErrorDialog
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
        setupToolBar()
        viewModel.fetchMatches()
    }

    private fun setupToolBar() {
        binding.tbMatch.inflateMenu(R.menu.match_action_bar)
        binding.tbMatch.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.miMatchActionBarRefresh -> {
                    println("miMatchActionBarRefresh")
                    true
                }
                R.id.miMatchActionBarSearch -> {
                    binding.tbMatch.visibility = View.GONE
                    binding.tbMatchSearch.visibility = View.VISIBLE
                    true
                }
                else -> false
            }
        }

        binding.btnMatchSearchBarClose.setOnClickListener {
            binding.tbMatchSearch.visibility = View.GONE
            binding.tbMatch.visibility = View.VISIBLE
            viewModel.changeMatchSearchKeyword("")
        }

        binding.etMatchSearch.addTextChangedListener {
            viewModel.changeMatchSearchKeyword(it.toString())
        }

    }

    private fun setupFetchMatchesObserver() {
        viewModel.fetchMatchesLiveData.observe(viewLifecycleOwner, {
//            if (it.isError()) FetchErrorDialog(it.errorMessage, this@MatchFragment).show(
//                childFragmentManager,
//                FetchErrorDialog.TAG
//            )
        })
    }

    private suspend fun setupMatchesObserver() {
        viewModel.matchPagedListLiveData.await().observe(viewLifecycleOwner, {
            matchPagedListAdapter.submitList(it)
        })
    }

    private fun setupMatchRecyclerView() {
        matchPagedListAdapter = MatchPagedListAdapter(this@MatchFragment)
        binding.rvMatch.adapter = matchPagedListAdapter
        binding.rvMatch.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onClick(view: View) {
        Navigation.findNavController(view)
            .navigate(MatchFragmentDirections.actionMatchFragmentToChatFragment(view.tag.toString().toLong()))
    }


    override fun onRefetch() {
        viewModel.fetchMatches()
    }
}


