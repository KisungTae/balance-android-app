package com.beeswork.balance.ui.click

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.marginEnd
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.ItemClickBinding
import com.beeswork.balance.databinding.ItemClickHeaderBinding
import com.beeswork.balance.databinding.LayoutLoadStateBinding
import com.beeswork.balance.internal.constant.EndPoint
import com.beeswork.balance.internal.util.GlideHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.util.*
import kotlin.random.Random


class ClickPagingDataAdapter(
    private val onClickListener: OnClickListener
) : PagingDataAdapter<ClickDomain, RecyclerView.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ClickDomain.Type.HEADER.ordinal -> HeaderViewHolder(
                ItemClickHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> ItemViewHolder(
                ItemClickBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                parent.context,
                onClickListener
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let { clickDomain ->
            when (holder) {
                is HeaderViewHolder -> holder.bind()
                is ItemViewHolder -> holder.bind(clickDomain)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getClick(position)?.type?.ordinal ?: ClickDomain.Type.ITEM.ordinal
    }

    fun getClick(position: Int): ClickDomain? {
        if (position >= itemCount) {
            return null
        }
        return getItem(position)
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ClickDomain>() {
            override fun areItemsTheSame(oldItem: ClickDomain, newItem: ClickDomain): Boolean =
                oldItem.swiperId == newItem.swiperId

            override fun areContentsTheSame(oldItem: ClickDomain, newItem: ClickDomain): Boolean =
                oldItem == newItem
        }
    }

    interface OnClickListener {
        fun onSelectClick(position: Int)
    }

    class HeaderViewHolder(
        private val binding: ItemClickHeaderBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {}
    }

    class ItemViewHolder(
        private val binding: ItemClickBinding,
        private val context: Context,
        private val onClickListener: OnClickListener
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(click: ClickDomain) {
            binding.clickName.text = click.name
//            val profilePhoto = EndPoint.ofPhoto(click.swiperId, click.profilePhotoKey)

//            TODO: remove me
//            val r = Random.nextInt(50)
//            val re = r % 5
//            val pic = when (re) {
//                0 -> R.drawable.person1
//                1 -> R.drawable.person2
//                2 -> R.drawable.person3
//                3 -> R.drawable.person4
//                4 -> R.drawable.person5
//                else -> R.drawable.person2
//            }
//
//            Glide.with(context)
//                .load(pic)
//                .apply(GlideHelper.profilePhotoGlideOptions())
//                .into(binding.ivClick)
        }

        override fun onClick(v: View?) {
            onClickListener.onSelectClick(absoluteAdapterPosition)
        }
    }


}