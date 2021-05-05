package com.beeswork.balance.ui.click

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.database.entity.Click
import com.beeswork.balance.databinding.ItemClickBinding
import com.beeswork.balance.internal.util.inflate


class ClickPagedListAdapter(
    private val onClickListener: OnClickListener
) : PagedListAdapter<Click, ClickPagedListAdapter.ViewHolder>(diffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.inflate(R.layout.item_click)
        return ViewHolder(
            ItemClickBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onClickListener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!)

    }

    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<Click>() {
            override fun areItemsTheSame(oldItem: Click, newItem: Click): Boolean =
                oldItem.swiperId == newItem.swiperId

            override fun areContentsTheSame(oldItem: Click, newItem: Click): Boolean =
                oldItem == newItem
        }
    }

    interface OnClickListener {
        fun onSwipe(swiperId: String)
    }

    class ViewHolder(
        private val binding: ItemClickBinding,
        private val onClickListener: OnClickListener
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(click: Click) {
//            itemView.tag = clicker.id
//            itemView.tvClicked.text = clicker.id.toString()
//            itemView.ivClicked.setImageResource(R.drawable.person1)
        }

        override fun onClick(v: View?) {
            onClickListener.onSwipe(v?.tag as String)
        }
    }
}