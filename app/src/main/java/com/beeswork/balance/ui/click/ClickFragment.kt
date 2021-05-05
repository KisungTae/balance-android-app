package com.beeswork.balance.ui.click

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.beeswork.balance.R
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.databinding.FragmentClickBinding
import com.beeswork.balance.ui.balancegame.BalanceGameDialog
import com.beeswork.balance.ui.common.ScopeFragment
import com.beeswork.balance.ui.common.ViewPagerChildFragment
import com.beeswork.balance.ui.dialog.FetchErrorDialog
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class ClickFragment : ScopeFragment(), KodeinAware,
    ClickPagedListAdapter.OnClickListener, BalanceGameDialog.BalanceGameListener,
    FetchErrorDialog.OnRetryListener, ViewPagerChildFragment {

    override val kodein by closestKodein()
    private val viewModelFactory: ClickViewModelFactory by instance()
    private lateinit var viewModel: ClickViewModel
    private lateinit var clickPagedListAdapter: ClickPagedListAdapter
    private lateinit var binding: FragmentClickBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentClickBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_click, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory).get(ClickViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = launch {

        clickPagedListAdapter = ClickPagedListAdapter(
            this@ClickFragment
        )

        binding.rvClick.adapter = clickPagedListAdapter
        val gridLayoutManager = GridLayoutManager(this@ClickFragment.context, 2)
        binding.rvClick.layoutManager = gridLayoutManager


        viewModel.fetchClicks()

        viewModel.clicks.await().observe(viewLifecycleOwner, { pagedClickedList ->
            clickPagedListAdapter.submitList(pagedClickedList)
        })

        viewModel.fetchClickListResponse.observe(viewLifecycleOwner, { fetchClickedListResponse ->

            when (fetchClickedListResponse.status) {
                Resource.Status.ERROR -> {
                    FetchErrorDialog(
                        fetchClickedListResponse.errorMessage,
                        this@ClickFragment
                    ).show(childFragmentManager, FetchErrorDialog.TAG)
                }
            }
        })
    }

    override fun onSwipe(swiperId: String) {
        BalanceGameDialog(swiperId, this@ClickFragment).show(
            childFragmentManager,
            BalanceGameDialog.TAG
        )
        viewModel.swipe(swiperId)
    }

    override fun onBalanceGameMatch(matchedPhotoKey: String) {
//        NewMatchDialog("", matchedPhotoKey).show(
//            childFragmentManager,
//            NewMatchDialog.TAG
//        )
    }

    override fun onRetry() {
        viewModel.fetchClicks()
    }

    override fun onFragmentSelected() {
        println("clicker fragment: onFragmentSelected")
    }

}