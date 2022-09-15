package com.beeswork.balance.ui.common.page

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.*
import com.beeswork.balance.databinding.ItemPageLoadStateBinding
import com.beeswork.balance.internal.util.MessageSource


// todo: before insert new items check if can scroll if yes then registerdataadapter and do recyferlview.scrollotposition(list.size),
//      only if it's loading to error

abstract class PageAdapter<Value : Any, VH : RecyclerView.ViewHolder>(
    diffCallback: DiffUtil.ItemCallback<Value>,
    private val pageLoadStatusListener: PageLoadStatusListener?
) : ListAdapter<Value, VH>(AsyncDifferConfig.Builder<Value>(diffCallback).build()) {

    private lateinit var pageMediator: PageMediator<Value>

    fun submitPageMediator(pageMediator: PageMediator<Value>, lifecycleOwner: LifecycleOwner) {
        this.pageMediator = pageMediator
        this.pageMediator.pageUIStateLiveData.observe(lifecycleOwner) { pageUIState ->
            if (pageUIState.items != null) {
                submitList(pageUIState.items)
            }
            val pageLoadStatus = pageUIState.pageLoadStatus
            pageLoadStatusListener?.onPageLoadStatusUpdated(pageLoadStatus)
            if (pageLoadStatus is PageLoadStatus.Loaded || pageLoadStatus is PageLoadStatus.Error) {
                pageMediator.clearPageLoad(pageLoadStatus.pageLoadType)
            }
        }
        loadPage(PageLoadType.REFRESH_PAGE)
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

    private fun reachedTopPreLoadDistance(layoutManager: RecyclerView.LayoutManager?): Boolean {
        if (layoutManager is LinearLayoutManager) {
            return layoutManager.findFirstVisibleItemPosition() <= 0
        }
        return false
    }

    private fun reachedBottomPreLoadDistance(layoutManager: RecyclerView.LayoutManager?): Boolean {
        if (layoutManager is LinearLayoutManager) {
            return layoutManager.findLastVisibleItemPosition() >= (itemCount - 1)
        }
        return false
    }

    class PageLoadStateViewHolder(
        private val binding: ItemPageLoadStateBinding,
        private val retry: (pageLoadType: PageLoadType) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pageLoadStatus: PageLoadStatus) {
            when (pageLoadStatus) {
                is PageLoadStatus.Loading -> {
                    binding.llPageLoadStateErrorWrapper.visibility = View.GONE
                    binding.llPageLoadStateLoadingWrapper.visibility = View.VISIBLE
                }
                is PageLoadStatus.Error -> {
                    binding.llPageLoadStateErrorWrapper.visibility = View.VISIBLE
                    binding.llPageLoadStateLoadingWrapper.visibility = View.GONE
                    val errorMessage = MessageSource.getMessage(pageLoadStatus.exception)
                    if (errorMessage != null) {
                        binding.tvPageLoadStateErrorMessage.text = errorMessage
                    }
                    binding.btnPageLoadStateErrorRetry.setOnClickListener {
                        retry.invoke(pageLoadStatus.pageLoadType)
                    }
                }
                else -> {
                }
            }
        }
    }
}