package com.beeswork.balance.ui.click

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.databinding.ItemClickBinding
import com.beeswork.balance.internal.util.GlideHelper
import com.bumptech.glide.Glide


class ClickPagingDataAdapter(
    private val onClickListener: OnClickListener
) : PagingDataAdapter<ClickDomain, ClickPagingDataAdapter.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemClickBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            parent.context,
            onClickListener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
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
        fun onSelect(position: Int)
    }

    class ViewHolder(
        private val binding: ItemClickBinding,
        private val context: Context,
        private val onClickListener: OnClickListener
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(click: ClickDomain) {
            Glide.with(context)
                .load(R.drawable.person3)
                .apply(GlideHelper.profilePhotoGlideOptions())
                .into(binding.ivClick)
//            binding.tvClicked.text = clicker.id.toString()
//            binding.ivClicked.setImageResource(R.drawable.person1)
        }

        override fun onClick(v: View?) {
            onClickListener.onSelect(absoluteAdapterPosition)
        }
    }
}