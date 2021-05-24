package com.beeswork.balance.ui.click

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginEnd
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.ItemClickBinding
import com.beeswork.balance.databinding.ItemClickHeaderBinding
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
                ItemClickHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                parent.context
            )
            else -> ItemViewHolder(
                ItemClickBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                parent.context,
                onClickListener
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            when (holder.itemViewType) {
                ClickDomain.Type.HEADER.ordinal -> (holder as HeaderViewHolder).bind()
                else -> {

                    (holder as ItemViewHolder).bind(it)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.type?.ordinal ?: ClickDomain.Type.ITEM.ordinal
    }

    fun getClick(position: Int): ClickDomain? {
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
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.tvClickHeaderTitle.text = context.getString(R.string.click_header_title)
        }
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
//            val profilePhoto = EndPoint.ofPhoto(click.swiperId, click.profilePhotoKey)

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
                .into(binding.ivClick)
        }

        override fun onClick(v: View?) {
            onClickListener.onSelectClick(absoluteAdapterPosition)
        }
    }
}