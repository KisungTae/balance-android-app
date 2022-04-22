package com.beeswork.balance.ui.swipefragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.ItemSwipeBinding
import com.beeswork.balance.databinding.ItemSwipeHeaderBinding
import com.beeswork.balance.internal.util.GlideHelper
import com.bumptech.glide.Glide
import kotlin.random.Random


class SwipePagingDataAdapter(
    private val onSwipeListener: OnSwipeListener
) : PagingDataAdapter<SwipeItemUIState, RecyclerView.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SwipeItemUIState.Type.HEADER.ordinal -> HeaderViewHolder(
                ItemSwipeHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> ItemViewHolder(
                ItemSwipeBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                parent.context,
                onSwipeListener
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let { swipeDomain ->
            when (holder) {
                is HeaderViewHolder -> holder.bind()
                is ItemViewHolder -> holder.bind(swipeDomain)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getSwipeDomain(position)?.type?.ordinal ?: SwipeItemUIState.Type.ITEM.ordinal
    }

    fun getSwipeDomain(position: Int): SwipeItemUIState? {
        if (position >= itemCount) {
            return null
        }
        return getItem(position)
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<SwipeItemUIState>() {
            override fun areItemsTheSame(oldItem: SwipeItemUIState, newItem: SwipeItemUIState): Boolean =
                oldItem.swiperId == newItem.swiperId

            override fun areContentsTheSame(oldItem: SwipeItemUIState, newItem: SwipeItemUIState): Boolean =
                oldItem == newItem
        }
    }

    interface OnSwipeListener {
        fun onSelectSwipe(position: Int)
    }

    class HeaderViewHolder(
        binding: ItemSwipeHeaderBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {}
    }

    class ItemViewHolder(
        private val binding: ItemSwipeBinding,
        private val context: Context,
        private val onSwipeListener: OnSwipeListener
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(swipeItemUIState: SwipeItemUIState) {
            binding.ivSwipeClickedIcon.isVisible = swipeItemUIState.clicked
            binding.ivSwipeLikedIcon.isVisible = !swipeItemUIState.clicked

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
            onSwipeListener.onSelectSwipe(absoluteAdapterPosition)
        }
    }


}