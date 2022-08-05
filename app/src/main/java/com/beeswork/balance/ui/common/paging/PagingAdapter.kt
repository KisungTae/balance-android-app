package com.beeswork.balance.ui.common.paging

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.beeswork.balance.internal.util.MessageSource
import kotlinx.coroutines.launch

abstract class PagingAdapter<Value : Any, VH : RecyclerView.ViewHolder>(
    diffCallback: DiffUtil.ItemCallback<Value>,
    private val context: Context
) : ListAdapter<Value, VH>(AsyncDifferConfig.Builder<Value>(diffCallback).build()) {

    private lateinit var pagingMediator: PagingMediator<Value>
    private lateinit var headerItemLoadStateAdapter: ItemLoadStateAdapter
    private lateinit var footerItemLoadStateAdapter: ItemLoadStateAdapter
    private lateinit var pageLoadStateAdapter: PageLoadStateAdapter

    private var reachedTop = true
    private var reachedBottom = true

    fun withLoadStateAdapters(
        headerItemLoadStateAdapter: ItemLoadStateAdapter,
        footerItemLoadStateAdapter: ItemLoadStateAdapter,
        pageLoadStateAdapter: PageLoadStateAdapter
    ): ConcatAdapter {
        this.pageLoadStateAdapter = pageLoadStateAdapter
        this.headerItemLoadStateAdapter = headerItemLoadStateAdapter
        this.footerItemLoadStateAdapter = footerItemLoadStateAdapter
        return ConcatAdapter(
            ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build(),
            this.headerItemLoadStateAdapter,
            this,
            this.footerItemLoadStateAdapter
        )
    }

    fun setupPagingMediator(pagingMediator: PagingMediator<Value>, lifecycleOwner: LifecycleOwner) {
        this.pagingMediator = pagingMediator
        this.pagingMediator.pageUIStateLiveData.observe(lifecycleOwner) { pageUIState ->
            when (pageUIState) {
                is PageUIState.Loading -> {
                    if (pageUIState.loadType == LoadType.REFRESH_PAGE || pageUIState.loadType == LoadType.REFRESH_FIRST_PAGE) {
                        return@observe
                    }
                    loadStateAdapter(pageUIState.loadType).onLoadStateUpdated(LoadState.Loading)
                }
                is PageUIState.Success -> {
                    reachedBottom = pageUIState.reachedBottom
                    reachedTop = pageUIState.reachedTop
                    submitList(pageUIState.items)

                    if (currentList.isEmpty()) {
                        pageLoadStateAdapter.onLoadStateUpdated(LoadState.Empty)
                    } else {
                        loadStateAdapter(pageUIState.loadType).onLoadStateUpdated(LoadState.Loaded)
                    }
                }
                is PageUIState.Error -> {
                    val errorLoadState = LoadState.Error(MessageSource.getMessage(context, pageUIState.throwable), pageUIState.loadType)
                    loadStateAdapter(pageUIState.loadType).onLoadStateUpdated(errorLoadState)
                }
            }
        }
//        triggerPageLoad(LoadType.REFRESH_DATA)
    }

    private fun loadStateAdapter(loadType: LoadType): LoadStateAdapter {
        return when (loadType) {
            LoadType.PREPEND_DATA -> headerItemLoadStateAdapter
            LoadType.APPEND_DATA -> footerItemLoadStateAdapter
            LoadType.REFRESH_DATA, LoadType.REFRESH_PAGE, LoadType.REFRESH_FIRST_PAGE, LoadType.PREPEND_DATA_AFTER_EMPTY_REFRESH -> pageLoadStateAdapter
        }
    }

    fun loadPage(loadType: LoadType) {
        pagingMediator.pager.loadPage(loadType)
    }

    fun refreshPage() {
        loadPage(LoadType.REFRESH_PAGE)
    }

    fun refreshData() {
        loadPage(LoadType.REFRESH_DATA)
    }

    fun refreshFirstPage() {
        loadPage(LoadType.REFRESH_FIRST_PAGE)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(-1) && dy < 0 && !reachedTop && headerItemLoadStateAdapter.itemCount <= 0) {
                    loadPage(LoadType.PREPEND_DATA)
                } else if (!recyclerView.canScrollVertically(1) && dy > 0 && !reachedBottom && footerItemLoadStateAdapter.itemCount <= 0) {
                    loadPage(LoadType.APPEND_DATA)
                }
            }
        })
    }
}