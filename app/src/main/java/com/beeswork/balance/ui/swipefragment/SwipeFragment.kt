package com.beeswork.balance.ui.swipefragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentSwipeBinding
import com.beeswork.balance.domain.uistate.swipe.SwipeUIState
import com.beeswork.balance.ui.balancegamedialog.CardBalanceGameListener
import com.beeswork.balance.ui.common.*
import com.beeswork.balance.ui.common.BalanceLoadStateAdapter
import com.beeswork.balance.ui.common.paging.LoadStateAdapter
import com.beeswork.balance.ui.common.paging.LoadType
import com.beeswork.balance.ui.common.paging.PagingAdapterListener
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.util.*

class SwipeFragment(
    private val cardBalanceGameListener: CardBalanceGameListener
) : BaseFragment(),
    KodeinAware,
    SwipePagingAdapter.SwipeViewHolderListener,
    ViewPagerChildFragment,
    PagingAdapterListener {

    override val kodein by closestKodein()
    private val viewModelFactory: SwipeViewModelFactory by instance()
    private lateinit var viewModel: SwipeViewModel
    private lateinit var binding: FragmentSwipeBinding
    private lateinit var swipePagingRefreshAdapter: PagingRefreshAdapter<SwipeUIState, RecyclerView.ViewHolder>
    private lateinit var swipePagingDataAdapter: SwipePagingDataAdapter
    private lateinit var footerLoadStateAdapter: BalanceLoadStateAdapter
    private lateinit var swipePagingInitialPageAdapter: PagingInitialPageAdapter<SwipeUIState, RecyclerView.ViewHolder>


    private lateinit var swipePagingAdapter: SwipePagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSwipeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SwipeViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = lifecycleScope.launch {
        setupSwipeRecyclerView()

//        setupSwipeRecyclerView()
//        setupSwipePagingInitialPageAdapter()
//        observeSwipePagingDataLiveData()
//        observeSwipePageInvalidationLiveData()
    }

    private fun setupSwipeRecyclerView() {
        swipePagingAdapter = SwipePagingAdapter(this@SwipeFragment, this@SwipeFragment, viewLifecycleOwner)
        binding.rvSwipe.adapter = swipePagingAdapter.withLoadStateAdapters(
            headerLoadStateAdapter = LoadStateAdapter { swipePagingAdapter.triggerPageLoad(LoadType.PREPEND) },
            footerLoadStateAdapter = LoadStateAdapter { swipePagingAdapter.triggerPageLoad(LoadType.APPEND) }
        )
        val gridLayoutManager = GridLayoutManager(this@SwipeFragment.context, SWIPE_PAGE_SPAN_COUNT)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (binding.rvSwipe.adapter?.getItemViewType(position)) {
                    R.layout.layout_load_state -> LOAD_STATE_SPAN_COUNT
                    R.layout.item_swipe_header -> SWIPE_HEADER_SPAN_COUNT
                    R.layout.item_swipe_footer -> SWIPE_FOOTER_SPAN_COUNT
                    else -> SWIPE_ITEM_SPAN_COUNT
                }
            }
        }
        binding.rvSwipe.layoutManager = gridLayoutManager
        binding.rvSwipe.itemAnimator = null
        swipePagingAdapter.setupPagingMediator(viewModel.getPagingMediator())
    }

    private suspend fun observeSwipePageInvalidationLiveData() {
        viewModel.swipePageInvalidationLiveData.await().observe(viewLifecycleOwner) {
            swipePagingRefreshAdapter.refresh()
        }
    }

    private fun observeSwipePagingDataLiveData() {
//        viewModel.initSwipePagingData().observe(viewLifecycleOwner, { pagingData ->
//            swipePagingRefreshAdapter.reset()
//            lifecycleScope.launch {
//                swipePagingDataAdapter.submitData(pagingData)
//            }
//        })
    }


    private fun setupSwipePagingInitialPageAdapter() {
//        binding.btnSwipeRetry.setOnClickListener {
//            swipePagingDataAdapter.retry()
//        }
//        swipePagingInitialPageAdapter = PagingInitialPageAdapter(
//            swipePagingDataAdapter,
//            binding.llSwipeInitialLoadingPage,
//            binding.llSwipeInitialErrorPage,
//            binding.llSwipeInitialEmptyPage,
//            binding.tvSwipeErrorMessage,
//            requireContext()
//        )
//        lifecycleScope.launch {
//            swipePagingDataAdapter.loadStateFlow.collect { loadState ->
//                swipePagingInitialPageAdapter.updateUI(loadState)
//            }
//        }
    }

    override fun onFragmentSelected() {
//        viewModel.test()
//        activity?.let { _activity ->
//            if (_activity is MainActivity) {
//                _activity.requestLocationPermission()
//            }
//        }
    }

    companion object {
        const val LOAD_STATE_SPAN_COUNT = 2
        const val SWIPE_HEADER_SPAN_COUNT = 2
        const val SWIPE_FOOTER_SPAN_COUNT = 2
        const val SWIPE_ITEM_SPAN_COUNT = 1
        const val SWIPE_PAGE_SPAN_COUNT = 2
    }

    override fun onClickSwipeViewHolder(position: Int) {
//        swipePagingDataAdapter.getSwipeDomain(position)?.let { swipe ->
//            swipePagingDataAdapter.refresh()
//            swipePagingDataAdapter.refresh()
//            viewModel.test()
//            SwipeBalanceGameDialog(click.swiperId, click.name, click.profilePhotoKey).show(
//                childFragmentManager,
//                SwipeBalanceGameDialog.TAG
//            )
//        }
    }

    override fun onPageLoading() {

    }

    override fun onPageEmpty() {

    }

    override fun onPageLoaded() {

    }

    override fun onPageLoadError(throwable: Throwable?) {

    }
}