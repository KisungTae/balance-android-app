package com.beeswork.balance.ui.swipefragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentSwipeBinding
import com.beeswork.balance.domain.uistate.swipe.SwipeItemUIState
import com.beeswork.balance.ui.balancegamedialog.CardBalanceGameListener
import com.beeswork.balance.ui.common.*
import com.beeswork.balance.ui.common.BalanceLoadStateAdapter
import com.beeswork.balance.ui.common.paging.LoadStatusAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.util.*

class SwipeFragment(
    private val cardBalanceGameListener: CardBalanceGameListener
) : BaseFragment(),
    KodeinAware,
    SwipeRecyclerViewAdapter.SwipeViewHolderListener,
    SwipePagingDataAdapter.OnSwipeListener,
    ViewPagerChildFragment {

    override val kodein by closestKodein()
    private val viewModelFactory: SwipeViewModelFactory by instance()
    private lateinit var viewModel: SwipeViewModel
    private lateinit var binding: FragmentSwipeBinding
    private lateinit var swipePagingRefreshAdapter: PagingRefreshAdapter<SwipeItemUIState, RecyclerView.ViewHolder>
    private lateinit var swipePagingDataAdapter: SwipePagingDataAdapter
    private lateinit var footerLoadStateAdapter: BalanceLoadStateAdapter
    private lateinit var swipePagingInitialPageAdapter: PagingInitialPageAdapter<SwipeItemUIState, RecyclerView.ViewHolder>


    private lateinit var swipeRecyclerViewAdapter: SwipeRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSwipeBinding.inflate(inflater)
        return binding.root
    }

    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SwipeViewModel::class.java)
        bindUI()
    }

    @ExperimentalPagingApi
    private fun bindUI() = lifecycleScope.launch {
        setupSwipeRecyclerView()
//        setupSwipeRecyclerView()
//        setupSwipePagingInitialPageAdapter()
//        observeSwipePagingDataLiveData()
//        observeSwipePageInvalidationLiveData()
    }

    private suspend fun observeSwipePageInvalidationLiveData() {
        viewModel.swipePageInvalidationLiveData.await().observe(viewLifecycleOwner) {
            swipePagingRefreshAdapter.refresh()
        }
    }

    @ExperimentalPagingApi
    private fun observeSwipePagingDataLiveData() {
        viewModel.initSwipePagingData().observe(viewLifecycleOwner, { pagingData ->
            swipePagingRefreshAdapter.reset()
            lifecycleScope.launch {
                swipePagingDataAdapter.submitData(pagingData)
            }
        })
    }

    private fun setupSwipeRecyclerView() {
        swipeRecyclerViewAdapter = SwipeRecyclerViewAdapter(this@SwipeFragment)
        binding.rvSwipe.adapter = swipeRecyclerViewAdapter.withLoadState(
            LoadStatusAdapter(swipeRecyclerViewAdapter::retry),
            LoadStatusAdapter(swipeRecyclerViewAdapter::retry)
        )
        val gridLayoutManager = GridLayoutManager(this@SwipeFragment.context, SWIPE_PAGE_SPAN_COUNT)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = binding.rvSwipe.adapter?.getItemViewType(position)
                if (viewType == R.layout.layout_load_state) {
                    return HEADER_SPAN_COUNT
                }
                return ITEM_SPAN_COUNT
            }
        }
        binding.rvSwipe.layoutManager = gridLayoutManager
        binding.rvSwipe.itemAnimator = null
    }

    private fun setupSwipePagingInitialPageAdapter() {
        binding.btnSwipeRetry.setOnClickListener {
            swipePagingDataAdapter.retry()
        }
        swipePagingInitialPageAdapter = PagingInitialPageAdapter(
            swipePagingDataAdapter,
            binding.llSwipeInitialLoadingPage,
            binding.llSwipeInitialErrorPage,
            binding.llSwipeInitialEmptyPage,
            binding.tvSwipeErrorMessage,
            requireContext()
        )
        lifecycleScope.launch {
            swipePagingDataAdapter.loadStateFlow.collect { loadState ->
                swipePagingInitialPageAdapter.updateUI(loadState)
            }
        }
    }

    override fun onFragmentSelected() {
//        viewModel.test()
//        activity?.let { _activity ->
//            if (_activity is MainActivity) {
//                _activity.requestLocationPermission()
//            }
//        }
    }

    override fun onSelectSwipe(position: Int) {
        swipePagingDataAdapter.getSwipeDomain(position)?.let { swipe ->
//            swipePagingDataAdapter.refresh()
//            swipePagingDataAdapter.refresh()
//            viewModel.test()
//            SwipeBalanceGameDialog(click.swiperId, click.name, click.profilePhotoKey).show(
//                childFragmentManager,
//                SwipeBalanceGameDialog.TAG
//            )
        }
    }

    companion object {
        const val HEADER_SPAN_COUNT = 2
        const val ITEM_SPAN_COUNT = 1
        const val FOOTER_SPAN_COUNT = 2
        const val SWIPE_PAGE_SPAN_COUNT = 2
    }

    override fun onClickSwipeViewHolder(position: Int) {
    }
}