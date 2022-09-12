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


    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var lifecycleOwner: LifecycleOwner
    private lateinit var pageMediator: PageMediator<Value>

    fun submitPageMediator(pageMediator: PageMediator<Value>, lifecycleOwner: LifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner
        this.pageMediator = pageMediator
        observePageUIStateLiveData()
    }

    private fun observePageUIStateLiveData() {
        pageMediator.pageUIStateLiveData.observe(lifecycleOwner) { pageUIState ->
            val pageLoadState = pageUIState.pageLoadState
            if (pageUIState.items != null) {
                submitList(pageUIState.items)
            }
            pageLoadStateListener?.onPageLoadStateUpdated(pageLoadState)
            if (pageLoadState is PageLoadState.Loaded || pageLoadState is PageLoadState.Error) {
                pageMediator.clearPageLoad(pageLoadState.pageLoadType)
            }
        }
    }

    fun loadPage(pageLoadType: PageLoadType) {

    }

    fun refreshPage() {
        loadPage(PageLoadType.REFRESH_PAGE)
    }

    fun refreshData() {
        loadPage(PageLoadType.REFRESH_DATA)
    }

    fun refreshFirstPage() {
        loadPage(PageLoadType.REFRESH_FIRST_PAGE)
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView

        if (recyclerView.layoutManager !is LinearLayoutManager) {
            throw RuntimeException("please provide valid layout manager for paging")
        }
        this.layoutManager = recyclerView.layoutManager as LinearLayoutManager

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy < 0 && !pageMediator.reachedTop() && reachedTopPreLoadDistance()) {
                    loadPage(PageLoadType.PREPEND_DATA)
                } else if (dy > 0 && !pageMediator.reachedBottom() && reachedBottomPreLoadDistance()) {
                    loadPage(PageLoadType.APPEND_DATA)
                }
            }
        })
    }

    private fun reachedTopPreLoadDistance(): Boolean {
        return layoutManager.findFirstVisibleItemPosition() <= 0
    }

    private fun reachedBottomPreLoadDistance(): Boolean {
        return layoutManager.findLastVisibleItemPosition() >= (itemCount - 1)
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