package com.beeswork.balance.ui.swipefragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.*
import com.beeswork.balance.domain.uistate.swipe.SwipeUIState
import com.beeswork.balance.ui.common.page.PageAdapter
import com.beeswork.balance.ui.common.page.PageLoadStatusListener

class SwipePageAdapter(
    private val swipeViewHolderListener: SwipeViewHolderListener,
    pageLoadStatusListener: PageLoadStatusListener?
) : PageAdapter<SwipeUIState, RecyclerView.ViewHolder>(diffCallback, pageLoadStatusListener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_swipe_header -> HeaderViewHolder(
                ItemSwipeHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            R.layout.item_page_load_state -> PageLoadStateViewHolder(
                ItemPageLoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                this::loadPage
            )
            else -> ItemViewHolder(
                ItemSwipeBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                swipeViewHolderListener
            )
        }
    }

    override fun onBindViewHolder(holderItem: RecyclerView.ViewHolder, position: Int) {
        when (holderItem) {
            is PageLoadStateViewHolder -> {
                val swipePageLoadState = currentList[position] as SwipeUIState.PageLoadState
                holderItem.bind(swipePageLoadState.pageLoadStatus)
            }
            is ItemViewHolder -> holderItem.bind(currentList[position] as SwipeUIState.Item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            is SwipeUIState.Header -> R.layout.item_swipe_header
            is SwipeUIState.PageLoadState -> R.layout.item_page_load_state
            is SwipeUIState.Item -> R.layout.item_swipe
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    fun getSwipeUIState(position: Int): SwipeUIState? {
        return currentList.getOrNull(position)
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<SwipeUIState>() {
            override fun areItemsTheSame(oldItem: SwipeUIState, newItem: SwipeUIState): Boolean {
                if (oldItem is SwipeUIState.Item && newItem is SwipeUIState.Item) {
                    return oldItem.id == newItem.id
                }
                return false
            }

            override fun areContentsTheSame(oldItem: SwipeUIState, newItem: SwipeUIState): Boolean {
                return oldItem == newItem
            }
        }
    }


    interface SwipeViewHolderListener {
        fun onClickSwipeViewHolder(position: Int)
    }

    class HeaderViewHolder(
        binding: ItemSwipeHeaderBinding,
    ) : RecyclerView.ViewHolder(binding.root)

    class ItemViewHolder(
        private val binding: ItemSwipeBinding,
        private val swipeViewHolderListener: SwipeViewHolderListener
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(swipeUIStateItem: SwipeUIState.Item) {
//            if (swipeItemUIState.clicked) {
//                binding.llSwipeClickedIconWrapper.visibility = View.VISIBLE
//            } else {
//                binding.llSwipeClickedIconWrapper.visibility = View.GONE
//            }
//            swipeItemUIState.swiperProfilePhotoUrl
//            Glide.with(App.getContext())
//                .load(pic)
//                .apply(GlideHelper.profilePhotoGlideOptions())
//                .into(binding.ivSwipe)
        }

        override fun onClick(v: View?) {
            swipeViewHolderListener.onClickSwipeViewHolder(absoluteAdapterPosition)
        }
    }
}