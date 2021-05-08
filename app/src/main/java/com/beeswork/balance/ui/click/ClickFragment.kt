package com.beeswork.balance.ui.click

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.beeswork.balance.databinding.FragmentClickBinding
import com.beeswork.balance.ui.balancegame.BalanceGameDialog
import com.beeswork.balance.ui.common.BaseFragment
import com.beeswork.balance.ui.common.PagingRefreshAdapter
import com.beeswork.balance.ui.common.ViewPagerChildFragment
import com.beeswork.balance.ui.dialog.FetchErrorDialog
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class ClickFragment : BaseFragment(),
    KodeinAware,
    ClickPagingDataAdapter.OnClickListener,
    BalanceGameDialog.BalanceGameListener,
    FetchErrorDialog.OnRetryListener,
    ViewPagerChildFragment {

    override val kodein by closestKodein()
    private val viewModelFactory: ClickViewModelFactory by instance()
    private lateinit var viewModel: ClickViewModel
    private lateinit var clickPagingDataAdapter: ClickPagingDataAdapter
    private lateinit var clickPagingRefreshAdapter: PagingRefreshAdapter<ClickDomain, ClickPagingDataAdapter.ViewHolder>
    private lateinit var binding: FragmentClickBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClickBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ClickViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = lifecycleScope.launch {
        setupClickRecyclerView()
        viewModel.initClickPagingData().observe(viewLifecycleOwner) {
            lifecycleScope.launch { clickPagingDataAdapter.submitData(it) }
        }


//        viewModel.initInvalidation().observe(viewLifecycleOwner) {
//            println("viewModel.initInvalidation().observe(viewLifecycleOwner)")
//        }


//        clickPagedListAdapter = ClickPagedListAdapter(
//            this@ClickFragment
//        )
//
//        binding.rvClick.adapter = clickPagedListAdapter
//        val gridLayoutManager = GridLayoutManager(this@ClickFragment.context, 2)
//        binding.rvClick.layoutManager = gridLayoutManager


//        viewModel.fetchClicks()
//
//        viewModel.clicks.await().observe(viewLifecycleOwner, { pagedClickedList ->
//            clickPagedListAdapter.submitList(pagedClickedList)
//        })
//
//        viewModel.fetchClickListResponse.observe(viewLifecycleOwner, { fetchClickedListResponse ->
//
//            when (fetchClickedListResponse.status) {
//                Resource.Status.ERROR -> {
//                    FetchErrorDialog(
//                        fetchClickedListResponse.errorMessage,
//                        this@ClickFragment
//                    ).show(childFragmentManager, FetchErrorDialog.TAG)
//                }
//            }
//        })
    }

    private fun setupClickRecyclerView() {
        clickPagingDataAdapter = ClickPagingDataAdapter(this)
        binding.rvClick.layoutManager = GridLayoutManager(this@ClickFragment.context, SPAN_COUNT)
        binding.rvClick.itemAnimator = null
        binding.rvClick.adapter = clickPagingDataAdapter
        clickPagingRefreshAdapter = PagingRefreshAdapter(binding.rvClick, clickPagingDataAdapter)
    }

    override fun onBalanceGameMatch(matchedPhotoKey: String) {
//        NewMatchDialog("", matchedPhotoKey).show(
//            childFragmentManager,
//            NewMatchDialog.TAG
//        )
    }

    override fun onRetry() {
//        viewModel.fetchClicks()
    }

    override fun onFragmentSelected() {
        println("clicker fragment: onFragmentSelected")
    }

    override fun onSelect(position: Int) {
        TODO("Not yet implemented")
    }

    companion object {
        const val SPAN_COUNT = 2
    }

}