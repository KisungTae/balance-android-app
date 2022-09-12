package com.beeswork.balance.ui.common.page

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.beeswork.balance.databinding.ItemPageLoadStateErrorBinding
import com.beeswork.balance.databinding.ItemPageLoadStateLoadingBinding
import java.lang.RuntimeException


// todo: before insert new items check if can scroll if yes then registerdataadapter and do recyferlview.scrollotposition(list.size),
//      only if it's loading to error

abstract class PageAdapter<Value : Any, VH : RecyclerView.ViewHolder>(
    diffCallback: DiffUtil.ItemCallback<Value>,
    private val pageLoadStateListener: PageLoadStateListener?
) : ListAdapter<Value, VH>(AsyncDifferConfig.Builder<Value>(diffCallback).build()) {

    private lateinit var pageMediator: PageMediator<Value>

    fun submitPageMediator(pageMediator: PageMediator<Value>, lifecycleOwner: LifecycleOwner) {
        this.pageMediator = pageMediator
        this.pageMediator.pageUIStateLiveData.observe(lifecycleOwner) { pageUIState ->
            if (pageUIState.items != null) {
                submitList(pageUIState.items)
            }
            val pageLoadState = pageUIState.pageLoadState
            pageLoadStateListener?.onPageLoadStateUpdated(pageLoadState)
            if (pageLoadState is PageLoadState.Loaded || pageLoadState is PageLoadState.Error) {
                pageMediator.clearPageLoad(pageLoadState.pageLoadType)
            }
        }
//        loadPage(PageLoadType.REFRESH_PAGE)
    }

    fun loadPage(pageLoadType: PageLoadType) {
        pageMediator.loadPage(pageLoadType)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy < 0 && !pageMediator.reachedTop() && reachedTopPreLoadDistance(recyclerView.layoutManager)) {
                    loadPage(PageLoadType.PREPEND_DATA)
                } else if (dy > 0 && !pageMediator.reachedBottom() && reachedBottomPreLoadDistance(recyclerView.layoutManager)) {
                    loadPage(PageLoadType.APPEND_DATA)
                }
            }
        })
    }

    private fun reachedTopPreLoadDistance(recyclerViewLayoutManager: RecyclerView.LayoutManager?): Boolean {
        if (recyclerViewLayoutManager is LinearLayoutManager) {
            return recyclerViewLayoutManager.findFirstVisibleItemPosition() <= 0
        }
        return false
    }

    private fun reachedBottomPreLoadDistance(recyclerViewLayoutManager: RecyclerView.LayoutManager?): Boolean {
        if (recyclerViewLayoutManager is LinearLayoutManager) {
            return recyclerViewLayoutManager.findLastVisibleItemPosition() >= (itemCount - 1)
        }
        return false
    }

    class PageLoadStateLoadingViewHolder(
        binding: ItemPageLoadStateLoadingBinding
    ) : RecyclerView.ViewHolder(binding.root)

    class PageLoadStateErrorViewHolder(
        private val binding: ItemPageLoadStateErrorBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(retry: () -> Unit, errorMessage: String?) {
            binding.btnPageLoadStateErrorRetry.setOnClickListener {
                retry.invoke()
            }
            if (errorMessage != null) {
                binding.tvPageLoadStateErrorMessage.text = errorMessage
            }
        }
    }
}