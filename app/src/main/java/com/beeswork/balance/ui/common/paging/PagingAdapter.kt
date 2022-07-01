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
    private val lifecycleOwner: LifecycleOwner,
    private val headerLoadStateAdapter: LoadStateAdapter,
    private val footerLoadStateAdapter: LoadStateAdapter
) : ListAdapter<T, VH>(AsyncDifferConfig.Builder<T>(diffCallback).build()) {


    // refreshed then set reachedEnd = false
    // when fetched pages with refresh, check if scroll position is at end, then trigger prepend or append

//    protected val items = mutableListOf<T>()
//    protected val items = mutableListOf<SwipeItemUIState>()

    private lateinit var pagingMediator: Pager.PagingMediator<T>

    private var reachedTop = true
    private var reachedBottom = true

    fun withLoadStateAdapters(): ConcatAdapter {
        return ConcatAdapter(
            ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build(),
            headerLoadStateAdapter,
            this,
            footerLoadStateAdapter
        )
    }

    fun setupPagingMediator(pagingMediator: Pager.PagingMediator<T>) {
        this.pagingMediator = pagingMediator
        this.pagingMediator.pageSnapshotLiveData.observe(lifecycleOwner) { pageSnapshot ->

        }
        triggerPageLoad(LoadType.INITIAL_LOAD)
    }

    private fun triggerPageLoad(loadType: LoadType) {
        when (loadType) {
            LoadType.PREPEND -> {
                if (headerLoadStateAdapter.itemCount > 0) {
                    return
                }
                headerLoadStateAdapter.loadState = LoadState.Loading()
            }
            LoadType.APPEND -> {
                if (footerLoadStateAdapter.itemCount > 0) {
                    return
                }
                footerLoadStateAdapter.loadState = LoadState.Loading()
            }
        }

        lifecycleOwner.lifecycleScope.launch {
            pagingMediator.pageLoadEventChannel.send(loadType)
        }
    }

    fun retry() {

        // todo: implement this
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(-1) && dy < 0 && !reachedTop) {
                    triggerPageLoad(LoadType.PREPEND)
                } else if (!recyclerView.canScrollVertically(1) && dy > 0 && !reachedBottom) {
                    triggerPageLoad(LoadType.APPEND)
                }
            }
        })
    }
}