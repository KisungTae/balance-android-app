package com.beeswork.balance.ui.clicker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.database.entity.Clicker
import com.beeswork.balance.databinding.ItemClickerBinding
import com.beeswork.balance.internal.util.inflate


class ClickerPagedListAdapter(
    private val onClickedListener: OnClickedListener
) : PagedListAdapter<Clicker, ClickerPagedListAdapter.ViewHolder>(diffCallback) {


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

        private val diffCallback = object : DiffUtil.ItemCallback<Clicker>() {
            override fun areItemsTheSame(oldItem: Clicker, newItem: Clicker): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Clicker, newItem: Clicker): Boolean =
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

        fun bind(clicker: Clicker) {
//            itemView.tag = clicker.id
//            itemView.tvClicked.text = clicker.id.toString()
//            itemView.ivClicked.setImageResource(R.drawable.person1)
        }

        override fun onClick(v: View?) {
            onClickedListener.onClickedClick(v?.tag as String)
        }
    }
}