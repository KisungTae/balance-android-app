package com.beeswork.balance.ui.common.paging

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.*
import com.beeswork.balance.domain.uistate.swipe.SwipeItemUIState
import com.beeswork.balance.ui.swipefragment.SwipePagingAdapter
import java.util.*

abstract class PagingAdapter<T : Any, I : Any, VH : RecyclerView.ViewHolder>(
    private val diffCallback: DiffUtil.ItemCallback<T>,
    private val pagingAdapterListener: PagingAdapterListener?
) : ListAdapter<T, VH>(AsyncDifferConfig.Builder<T>(diffCallback).build()) {


    // refreshed then set reachedEnd = false
    // when fetched pages with refresh, check if scroll position is at end, then trigger prepend or append

//    protected val items = mutableListOf<T>()
//    protected val items = mutableListOf<SwipeItemUIState>()

    private var headerLoadStateAdapter: LoadStateAdapter? = null
    private var footerLoadStateAdapter: LoadStateAdapter? = null
    private var headerAdapter: RecyclerView.Adapter<VH>? = null
    private lateinit var pager: Pager<T, I>


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

    fun setupPager(lifecycleOwner: LifecycleOwner, pager: Pager<T, I>) {
        this.pager = pager
        this.pager.pageLiveData.observe(lifecycleOwner) { page ->

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
                if (!recyclerView.canScrollVertically(-1) && dy < 0) {
                    // reached the top

                    println("!recyclerView.canScrollVertically(-1) && dy < 0")
                } else if (!recyclerView.canScrollVertically(1) && dy > 0) {
                    // reached the bottom

//                    footerLoadStateAdapter?.loadState = LoadState.Loading(LoadType.APPEND)

//                    println(items.subList(10, 30))

                    val newList = mutableListOf<SwipeItemUIState>()

//                    val list = items.subList(20, items.lastIndex)
                    for (i in 0..40) {
//                        list.add(SwipeItemUIState(UUID.randomUUID(), false, null))
                    }


                }

//                if (dy > 0) {
//                    println("scroll up!!!!!!")
//                } else {
//                    println("scroll down!!!!!!")
//                }

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