package com.beeswork.balance.ui.common.page

import androidx.recyclerview.widget.*
import com.beeswork.balance.databinding.ItemPageLoadStateErrorBinding
import com.beeswork.balance.databinding.ItemPageLoadStateLoadingBinding
import java.lang.RuntimeException


// todo: 1. keep the loading state when refresh or other data load
//       2. if error load state at the bottom but successful prepend, then get rid of the bottom error state
//       3. every data load, needs page_refresh after it successfully loads data and udpate data in database
//       4.


// todo: before insert new items check if can scroll if yes then registerdataadapter and do recyferlview.scrollotposition(list.size),
//      only if it's loading to error

abstract class PageAdapter<Value : Any, VH : RecyclerView.ViewHolder>(
    diffCallback: DiffUtil.ItemCallback<Value>
) : ListAdapter<Value, VH>(AsyncDifferConfig.Builder<Value>(diffCallback).build()) {


    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView



    fun submitPageUIState(pageUIState: PageUIState<Value>) {


        when (pageUIState) {
            is PageUIState.Loading -> {
                if (pageUIState.pageLoadType == PageLoadType.REFRESH_DATA || pageUIState.pageLoadType == PageLoadType.REFRESH_PREPEND_DATA) {
//                    pageLoadStateAdapter.onLoadStateUpdated(LoadState.Loading)
                }
            }
            is PageUIState.Success -> {
//                reachedBottom = pageUIState.reachedBottom
//                reachedTop = pageUIState.reachedTop


//                val a = PageLoadStatus.Loaded(PageLoadType.REFRESH_FIRST_PAGE, true)


//                submitList(pageUIState.items)
//                loadTypeQueue.remove(pageUIState.originalPageLoadType)


                // todo: remove loadstate even if it's empty?
//                if (currentList.isEmpty()) {
//                    pageLoadStateAdapter.onLoadStateUpdated(LoadState.Empty)
//                } else {
//                    loadStateAdapter(pageUIState.loadType).onLoadStateUpdated(LoadState.Loaded)
//                }


            }
            is PageUIState.Error -> {
//                loadTypeQueue.remove(pageUIState.originalPageLoadType)
//                val errorLoadState = LoadState.Error(MessageSource.getMessage(pageUIState.throwable), pageUIState.loadType)
//                loadStateAdapter(pageUIState.loadType).onLoadStateUpdated(errorLoadState)
            }
        }
    }

//    fun setupPagingMediator(pageMediator: PageMediator<Value>) {
//        this.pageMediator = pageMediator
//        loadPage(LoadType.REFRESH_DATA)
//    }

    fun loadPage(pageLoadType: PageLoadType) {
//        if (!loadTypeQueue.contains(pageLoadType)) {
//            pageMediator.pager.loadPage(pageLoadType)
//            loadTypeQueue.add(pageLoadType)
//        }
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

//                if (dy < 0 && !reachedTop && reachedTopPreLoadDistance()) {
//                    loadPage(PageLoadType.PREPEND_DATA)
//                } else if (dy > 0 && !reachedBottom && reachedBottomPreLoadDistance()) {
//                    loadPage(PageLoadType.APPEND_DATA)
//                }
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