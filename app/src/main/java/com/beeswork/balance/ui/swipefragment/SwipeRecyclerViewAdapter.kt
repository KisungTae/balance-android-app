package com.beeswork.balance.ui.swipefragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.ItemSwipeBinding
import com.beeswork.balance.domain.uistate.swipe.SwipeItemUIState
import com.beeswork.balance.internal.util.GlideHelper
import com.beeswork.balance.ui.common.paging.PagingAdapter
import com.bumptech.glide.Glide
import java.util.*
import kotlin.random.Random

class SwipeRecyclerViewAdapter(
    private val swipeViewHolderListener: SwipeViewHolderListener
) : PagingAdapter<SwipeItemUIState, SwipeRecyclerViewAdapter.ViewHolder>() {

    init {
        // todo: remove me
        for (i in 0..100) {
            items.add(SwipeItemUIState(UUID.randomUUID(), false, null))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSwipeBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            parent.context,
            swipeViewHolderListener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        items.getOrNull(position)?.let { swipeItemUIState ->
            holder.bind(swipeItemUIState)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_swipe
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    interface SwipeViewHolderListener {
        fun onClickSwipeViewHolder(position: Int)
    }

    class ViewHolder(
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