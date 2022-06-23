package com.beeswork.balance.ui.common.paging

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class PagingAdapter<T : Any, VH : RecyclerView.ViewHolder>(
    private val diffCallback: DiffUtil.ItemCallback<T>
) : RecyclerView.Adapter<VH>() {


    // refreshed then set reachedEnd = false
    // when fetched pages with refresh, check if scroll position is at end, then trigger prepend or append

    protected val items = mutableListOf<T>()

    private var headerLoadStateAdapter: LoadStateAdapter? = null
    private var footerLoadStateAdapter: LoadStateAdapter? = null
    private var headerAdapter: RecyclerView.Adapter<VH>? = null


    fun withLoadStateAdapters(
        headerLoadStateAdapter: LoadStateAdapter,
        footerLoadStateAdapter: LoadStateAdapter
    ): ConcatAdapter {
        this.headerLoadStateAdapter = headerLoadStateAdapter
        this.footerLoadStateAdapter = footerLoadStateAdapter
        return ConcatAdapter(
            ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build(),
            headerLoadStateAdapter,
            this,
            footerLoadStateAdapter
        )
    }

    fun retry() {
        // todo: implement this
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    println("scroll up!!!!!!")
                } else {
                    println("scroll down!!!!!!")
                }

                //                val layoutManager = recyclerView.layoutManager!!
//                val totalCount = layoutManager.itemCount
//                val lastVisibleItemPosition = if (layoutManager is LinearLayoutManager) {
//                    layoutManager.findLastVisibleItemPosition()
//                } else if (layoutManager is GridLayoutManager) {
//                    layoutManager.findLastVisibleItemPosition()
//                } else {
//                    0
//                }
//
//                val firstVisibleItemPosition = if (layoutManager is LinearLayoutManager) {
//                    layoutManager.findFirstVisibleItemPosition()
//                } else if (layoutManager is GridLayoutManager) {
//                    layoutManager.findFirstVisibleItemPosition()
//                } else {
//                    0
//                }
            }
        })
    }
}