package com.beeswork.balance.ui.common

import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView

class PagingRefreshAdapter<T: Any, VH: RecyclerView.ViewHolder>(
    recyclerView: RecyclerView,
    private val pagingDataAdapter: PagingDataAdapter<T, VH>
): RecyclerView.OnScrollListener() {

    private var scrolling = false
    private var refresh = false

    init {
        recyclerView.addOnScrollListener(this)
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        when (newState) {
            RecyclerView.SCROLL_STATE_IDLE -> {
                scrolling = false
                refreshAdapter()
            }
            RecyclerView.SCROLL_STATE_DRAGGING -> {
                scrolling = true
            }
        }
    }

    private fun refreshAdapter() {
        if (!scrolling && refresh) {
            pagingDataAdapter.refresh()
            refresh = false
        }
    }

    fun refresh() {
        refresh = true
        refreshAdapter()
    }

    fun reset() {
        refresh = false
    }
}