package com.beeswork.balance.ui.swipefragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.ItemSwipeBinding
import com.beeswork.balance.databinding.ItemSwipeHeaderBinding
import com.beeswork.balance.domain.uistate.swipe.SwipeItemUIState
import com.beeswork.balance.internal.util.GlideHelper
import com.beeswork.balance.ui.common.paging.PagingAdapter
import com.beeswork.balance.ui.common.paging.PagingAdapterListener
import com.bumptech.glide.Glide
import java.util.*
import kotlin.random.Random

class SwipePagingAdapter(
    private val swipeViewHolderListener: SwipeViewHolderListener,
    pagingAdapterListener: PagingAdapterListener
) : PagingAdapter<SwipeItemUIState, Long, RecyclerView.ViewHolder>(diffCallback, pagingAdapterListener) {

    init {
        val items = mutableListOf<SwipeItemUIState>()
        // todo: remove me
        items.add(SwipeItemUIState.asHeader())
        for (i in 0..30) {
            items.add(SwipeItemUIState(i.toLong(), UUID.randomUUID(), false, null))
        }
        submitList(items)
        currentList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_swipe_header -> HeaderViewHolder(
                ItemSwipeHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
            is ItemViewHolder -> holderItem.bind(currentList[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position].type) {
            SwipeItemUIState.Type.HEADER -> R.layout.item_swipe_header
            SwipeItemUIState.Type.ITEM -> R.layout.item_swipe
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<SwipeItemUIState>() {
            override fun areItemsTheSame(oldItem: SwipeItemUIState, newItem: SwipeItemUIState): Boolean =
                oldItem.swiperId == newItem.swiperId

            override fun areContentsTheSame(oldItem: SwipeItemUIState, newItem: SwipeItemUIState): Boolean =
                oldItem == newItem
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

    class ItemViewHolder(
        private val binding: ItemSwipeBinding,
        private val context: Context,
        private val swipeViewHolderListener: SwipeViewHolderListener
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(swipeItemUIState: SwipeItemUIState) {
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