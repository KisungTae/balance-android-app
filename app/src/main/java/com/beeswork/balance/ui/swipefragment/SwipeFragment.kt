package com.beeswork.balance.ui.swipefragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.beeswork.balance.R
import com.beeswork.balance.databinding.FragmentSwipeBinding
import com.beeswork.balance.domain.uistate.swipe.SwipeUIState
import com.beeswork.balance.internal.util.MessageSource
import com.beeswork.balance.ui.balancegamedialog.CardBalanceGameListener
import com.beeswork.balance.ui.common.*
import com.beeswork.balance.ui.common.page.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.util.*

class SwipeFragment(
    private val cardBalanceGameListener: CardBalanceGameListener
) : BaseFragment(),
    KodeinAware,
    SwipePageAdapter.SwipeViewHolderListener,
    ViewPagerChildFragment,
    PageLoadStateListener {

    override val kodein by closestKodein()
    private val viewModelFactory: SwipeViewModelFactory by instance()
    private lateinit var viewModel: SwipeViewModel
    private lateinit var binding: FragmentSwipeBinding
    private lateinit var swipePageAdapter: SwipePageAdapter

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
        val gridLayoutManager = GridLayoutManager(this@SwipeFragment.context, SWIPE_PAGE_SPAN_COUNT)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (binding.rvSwipe.adapter?.getItemViewType(position)) {
                    R.layout.item_page_load_state_loading -> SWIPE_PAGE_LOAD_STATE_LOADING_SPAN_COUNT
                    R.layout.item_page_load_state_error -> SWIPE_PAGE_LOAD_STATE_ERROR_SPAN_COUNT
                    R.layout.item_swipe_header -> SWIPE_HEADER_SPAN_COUNT
                    else -> SWIPE_ITEM_SPAN_COUNT
                }
            }
        }
        binding.rvSwipe.layoutManager = gridLayoutManager
        binding.rvSwipe.itemAnimator = null
        swipePageAdapter = SwipePageAdapter(this@SwipeFragment, this@SwipeFragment)
        binding.rvSwipe.adapter = swipePageAdapter
        swipePageAdapter.submitPageMediator(viewModel.initPageMediator(), viewLifecycleOwner)
    }

    private suspend fun observeSwipePageInvalidationLiveData() {
        viewModel.swipePageInvalidationLiveData.await().observe(viewLifecycleOwner) {
//            swipePagingRefreshAdapter.refresh()
        }
    }

    override fun onFragmentSelected() {
        val items = mutableListOf<SwipeUIState>()
        items.add(SwipeUIState.Item(0, UUID.randomUUID(), false, null))
        swipePageAdapter.submitList(items)
    }

    override fun onClickSwipeViewHolder(position: Int) {
        viewModel.test()
    }

    override fun onPageLoadStateUpdated(pageLoadState: PageLoadState) {
        if (pageLoadState.pageLoadType != PageLoadType.PREPEND_DATA && pageLoadState.pageLoadType != PageLoadType.REFRESH_PREPEND_DATA) {
            return
        }
        resetPageLayouts()
        when (pageLoadState) {
            is PageLoadState.Loading -> {
                binding.llSwipePageLoading.visibility = View.VISIBLE
            }
            is PageLoadState.Loaded -> {
                if (pageLoadState.numOfItemsLoaded <= 0) {
                    binding.llSwipePageEmpty.visibility = View.VISIBLE
                } else {
                    binding.rvSwipe.visibility = View.VISIBLE
                }
            }
            is PageLoadState.Error -> {
                binding.llSwipePageError.visibility = View.VISIBLE
                binding.tvSwipePageErrorMessage.text = MessageSource.getMessage(pageLoadState.exception, R.string.error_message_generic)
                binding.btnSwipePageErrorRetry.setOnClickListener {
                    swipePageAdapter.loadPage(pageLoadState.pageLoadType)
                }
            }
        }
    }

    private fun resetPageLayouts() {
        binding.rvSwipe.visibility = View.GONE
        binding.llSwipePageEmpty.visibility = View.GONE
        binding.llSwipePageError.visibility = View.GONE
        binding.llSwipePageLoading.visibility = View.GONE
    }

    companion object {
        const val SWIPE_PAGE_LOAD_STATE_LOADING_SPAN_COUNT = 2
        const val SWIPE_PAGE_LOAD_STATE_ERROR_SPAN_COUNT = 2
        const val SWIPE_HEADER_SPAN_COUNT = 2
        const val SWIPE_ITEM_SPAN_COUNT = 1
        const val SWIPE_PAGE_SPAN_COUNT = 2
    }
}