package com.beeswork.balance.ui.swipefragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.databinding.FragmentSwipeBinding
import com.beeswork.balance.ui.common.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.util.*

class SwipeFragment : BaseFragment(),
    KodeinAware,
    SwipePagingDataAdapter.OnSwipeListener,
    ViewPagerChildFragment {

    override val kodein by closestKodein()
    private val viewModelFactory: SwipeViewModelFactory by instance()
    private lateinit var viewModel: SwipeViewModel
    private lateinit var binding: FragmentSwipeBinding
    private lateinit var swipePagingRefreshAdapter: PagingRefreshAdapter<SwipeDomain, RecyclerView.ViewHolder>
    private lateinit var swipePagingDataAdapter: SwipePagingDataAdapter
    private lateinit var footerLoadStateAdapter: BalanceLoadStateAdapter
    private lateinit var swipePagingInitialPageAdapter: PagingInitialPageAdapter<SwipeDomain, RecyclerView.ViewHolder>

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
        setupSwipePagingInitialPageAdapter()
        observeSwipePagingDataLiveData()
        observeSwipePageInvalidationLiveData()
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
        swipePagingDataAdapter = SwipePagingDataAdapter(this@SwipeFragment)
        footerLoadStateAdapter = BalanceLoadStateAdapter(swipePagingDataAdapter::retry)
        binding.rvSwipe.adapter = swipePagingDataAdapter.withLoadStateFooter(
            footer = footerLoadStateAdapter
        )

        val gridLayoutManager = GridLayoutManager(this@SwipeFragment.context, SWIPE_PAGE_SPAN_COUNT)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // NOTE 1.  if you call clickPagingDataAdapter.getItemViewType(position), it will cause an infinite loop of load()
                //          when it reaches to the max page size. I don't know why but below code only gives the header span count of 2
                // NOTE 2.  if you put delay(10000) in loadSwipes(), then it will make the first card of current pages span of 2, but
                //          prepend will always have pages to load so it won't call API so no delay when scroll up

                if (position == swipePagingDataAdapter.itemCount && footerLoadStateAdapter.itemCount > 0) {
                    return FOOTER_SPAN_COUNT
                }

                return if (position == 0) {
                    HEADER_SPAN_COUNT
                } else {
                    ITEM_SPAN_COUNT
                }
            }
        }
        binding.rvSwipe.layoutManager = gridLayoutManager
        binding.rvSwipe.itemAnimator = null
        swipePagingRefreshAdapter = PagingRefreshAdapter(binding.rvSwipe, swipePagingDataAdapter)
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
    }

    override fun onSelectSwipe(position: Int) {
        swipePagingDataAdapter.getSwipeDomain(position)?.let { swipe ->
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
}