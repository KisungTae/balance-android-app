package com.beeswork.balance.ui.common.paging

import android.renderscript.Sampler
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.beeswork.balance.domain.uistate.swipe.SwipeItemUIState
import com.beeswork.balance.ui.swipefragment.SwipePagingAdapter
import kotlinx.coroutines.launch
import java.util.*

abstract class PagingAdapter<T : Any, VH : RecyclerView.ViewHolder>(
    diffCallback: DiffUtil.ItemCallback<T>,
    private val pagingAdapterListener: PagingAdapterListener?,
    private val lifecycleOwner: LifecycleOwner
) : ListAdapter<T, VH>(AsyncDifferConfig.Builder<T>(diffCallback).build()) {


    // refreshed then set reachedEnd = false
    // when fetched pages with refresh, check if scroll position is at end, then trigger prepend or append
    // refresh the middle pages and nothing returned from, then it is empty page, then trigger prepend because refresh already append

    private lateinit var pagingMediator: Pager.PagingMediator<T>
    private lateinit var headerLoadStateAdapter: LoadStateAdapter
    private lateinit var footerLoadStateAdapter: LoadStateAdapter


    private var reachedTop = true
    private var reachedBottom = true

    fun withLoadStateAdapters(headerLoadStateAdapter: LoadStateAdapter, footerLoadStateAdapter: LoadStateAdapter): ConcatAdapter {
        this.headerLoadStateAdapter = headerLoadStateAdapter
        this.footerLoadStateAdapter = footerLoadStateAdapter
        return ConcatAdapter(
            ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build(),
            this.headerLoadStateAdapter,
            this,
            this.footerLoadStateAdapter
        )
    }

    fun setupPagingMediator(pagingMediator: Pager.PagingMediator<T>) {
        this.pagingMediator = pagingMediator
        this.pagingMediator.pageSnapshotLiveData.observe(lifecycleOwner) { pageSnapshot ->

        }
        triggerPageLoad(LoadType.INITIAL_LOAD)
    }

    fun triggerPageLoad(loadType: LoadType) {
        when (loadType) {
            LoadType.PREPEND -> {
                headerLoadStateAdapter.loadState = LoadState.Loading()
            }
            LoadType.APPEND -> {
                footerLoadStateAdapter.loadState = LoadState.Loading()
            }
            LoadType.REFRESH_PAGE -> {
            }
            else -> {
                pagingAdapterListener?.onPageLoading()
            }
        }
        lifecycleOwner.lifecycleScope.launch {
            pagingMediator.pageLoadEventChannel.send(loadType)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(-1) && dy < 0 && !reachedTop && headerLoadStateAdapter.itemCount <= 0) {
                    triggerPageLoad(LoadType.PREPEND)
                } else if (!recyclerView.canScrollVertically(1) && dy > 0 && !reachedBottom && footerLoadStateAdapter.itemCount <= 0) {
                    triggerPageLoad(LoadType.APPEND)
                }
            }
        })
    }
}