package com.beeswork.balance.ui.clicker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.database.entity.Click
import com.beeswork.balance.databinding.ItemClickerBinding
import com.beeswork.balance.internal.util.inflate


class ClickerPagedListAdapter(
    private val onClickedListener: OnClickedListener
) : PagedListAdapter<Click, ClickerPagedListAdapter.ViewHolder>(diffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.inflate(R.layout.item_clicker)
        return ViewHolder(
            ItemClickerBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onClickedListener
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

    interface OnClickedListener {
        fun onClickedClick(swipedId: String)
    }

    class ViewHolder(
        private val binding: ItemClickerBinding,
        private val onClickedListener: OnClickedListener
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
            onClickedListener.onClickedClick(v?.tag as String)
        }
    }
}