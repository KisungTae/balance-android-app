package com.beeswork.balance.ui.clicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.beeswork.balance.R
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.databinding.FragmentClickerBinding
import com.beeswork.balance.ui.balancegame.BalanceGameDialog
import com.beeswork.balance.ui.common.ScopeFragment
import com.beeswork.balance.ui.dialog.FetchErrorDialog
import com.beeswork.balance.ui.dialog.MatchDialog
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class ClickerFragment : ScopeFragment(), KodeinAware,
    ClickerPagedListAdapter.OnClickedListener, BalanceGameDialog.BalanceGameListener,
    FetchErrorDialog.OnRetryListener {

    override val kodein by closestKodein()
    private val viewModelFactory: ClickerViewModelFactory by instance()
    private lateinit var viewModel: ClickedViewModel
    private lateinit var clickerPagedListAdapter: ClickerPagedListAdapter
    private lateinit var binding: FragmentClickerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentClickerBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_clicker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory).get(ClickedViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = launch {

        clickerPagedListAdapter = ClickerPagedListAdapter(
            this@ClickerFragment
        )

        binding.rvClicked.adapter = clickerPagedListAdapter
        val gridLayoutManager = GridLayoutManager(this@ClickerFragment.context, 2)
        binding.rvClicked.layoutManager = gridLayoutManager


        viewModel.fetchClickedList()

        viewModel.clickedList.await().observe(viewLifecycleOwner, { pagedClickedList ->
            clickerPagedListAdapter.submitList(pagedClickedList)
        })

        viewModel.fetchClickerListResponse.observe(viewLifecycleOwner, { fetchClickedListResponse ->

            when (fetchClickedListResponse.status) {
                Resource.Status.ERROR -> {
                    FetchErrorDialog(
                        fetchClickedListResponse.errorMessage,
                        this@ClickerFragment
                    ).show(childFragmentManager, FetchErrorDialog.TAG)
                }
            }
        })
    }

    override fun onClickedClick(swipedId: String) {
        BalanceGameDialog(swipedId, this@ClickerFragment).show(
            childFragmentManager,
            BalanceGameDialog.TAG
        )
        viewModel.swipe(swipedId)
    }

    override fun onBalanceGameMatch(matchedPhotoKey: String) {
        MatchDialog("", matchedPhotoKey).show(
            childFragmentManager,
            MatchDialog.TAG
        )
    }

    override fun onRetry() {
        viewModel.fetchClickedList()
    }

}