package com.beeswork.balance.ui.common.paging

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class PagingAdapter<T : Any, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    protected val items = mutableListOf<T>()

    private var headerLoadStatusAdapter: LoadStatusAdapter? = null
    private var footerLoadStatusAdapter: LoadStatusAdapter? = null


    fun withLoadState(headerLoadStatusAdapter: LoadStatusAdapter, footerLoadStatusAdapter: LoadStatusAdapter): ConcatAdapter {
        this.headerLoadStatusAdapter = headerLoadStatusAdapter
        this.footerLoadStatusAdapter = footerLoadStatusAdapter
        return ConcatAdapter(ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build(), headerLoadStatusAdapter, this, footerLoadStatusAdapter)
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