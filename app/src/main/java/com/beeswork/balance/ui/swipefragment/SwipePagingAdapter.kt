package com.beeswork.balance.ui.swipefragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.ItemSwipeBinding
import com.beeswork.balance.databinding.ItemSwipeFooterBinding
import com.beeswork.balance.databinding.ItemSwipeHeaderBinding
import com.beeswork.balance.domain.uistate.swipe.SwipeUIState
import com.beeswork.balance.internal.util.GlideHelper
import com.beeswork.balance.ui.common.paging.PagingAdapter
import com.bumptech.glide.Glide
import java.util.*
import kotlin.random.Random

class SwipePagingAdapter(
    private val swipeViewHolderListener: SwipeViewHolderListener,
    lifecycleOwner: LifecycleOwner,
    context: Context
) : PagingAdapter<SwipeUIState, RecyclerView.ViewHolder>(
    diffCallback,
    lifecycleOwner,
    context
) {

    init {
        val items = mutableListOf<SwipeUIState>()
        // todo: remove me
        items.add(SwipeUIState.Header())
        for (i in 0..30) {
            items.add(SwipeUIState.Item(i.toLong(), UUID.randomUUID(), false, null))
        }
        items.add(SwipeUIState.Footer())
        submitList(items)
        currentList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_swipe_header -> HeaderViewHolder(
                ItemSwipeHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            R.layout.item_swipe_footer -> FooterViewHolder(
                ItemSwipeFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> ItemViewHolder(
                ItemSwipeBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                parent.context,
                swipeViewHolderListener
            )
        }
    }

    override fun onBindViewHolder(holderItem: RecyclerView.ViewHolder, position: Int) {
        when (holderItem) {
            is HeaderViewHolder -> holderItem.bind()
            is FooterViewHolder -> holderItem.bind()
            is ItemViewHolder -> holderItem.bind(currentList[position] as SwipeUIState.Item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            is SwipeUIState.Header -> R.layout.item_swipe_header
            is SwipeUIState.Item -> R.layout.item_swipe
            is SwipeUIState.Footer -> R.layout.item_swipe_footer
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<SwipeUIState>() {
            override fun areItemsTheSame(oldItem: SwipeUIState, newItem: SwipeUIState): Boolean {
                return oldItem.key == newItem.key
            }

            override fun areContentsTheSame(oldItem: SwipeUIState, newItem: SwipeUIState): Boolean {
                val result = oldItem == newItem
                println("result: $result")
                return result
            }
        }
    }

    interface SwipeViewHolderListener {
        fun onClickSwipeViewHolder(position: Int)
    }

    class HeaderViewHolder(
        binding: ItemSwipeHeaderBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {}
    }

    class FooterViewHolder(
        binding: ItemSwipeFooterBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {}
    }

    class ItemViewHolder(
        private val binding: ItemSwipeBinding,
        private val context: Context,
        private val swipeViewHolderListener: SwipeViewHolderListener
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(swipeUIState: SwipeUIState.Item) {
//            binding.llSwipeClickedIconWrapper.visibility = View.VISIBLE
//            if (swipeItemUIState.clicked) {
//                binding.llSwipeClickedIconWrapper.visibility = View.VISIBLE
//            } else {
//                binding.llSwipeClickedIconWrapper.visibility = View.GONE
//            }

//            swipeItemUIState.swiperProfilePhotoUrl

//            TODO: remove me
            val r = Random.nextInt(50)
            val re = r % 5
            val pic = when (re) {
                0 -> R.drawable.person1
                1 -> R.drawable.person2
                2 -> R.drawable.person3
                3 -> R.drawable.person4
                4 -> R.drawable.person5
                else -> R.drawable.person2
            }

            Glide.with(context)
                .load(pic)
                .apply(GlideHelper.profilePhotoGlideOptions())
                .into(binding.ivSwipe)
        }

        override fun onClick(v: View?) {
            swipeViewHolderListener.onClickSwipeViewHolder(absoluteAdapterPosition)
        }
    }
}