package com.beeswork.balance.ui.clicked

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.database.entity.Clicker
import com.beeswork.balance.internal.util.inflate

import kotlinx.android.synthetic.main.item_clicked.view.*


class ClickedPagedListAdapter(
    private val onClickedListener: OnClickedListener
): PagedListAdapter<Clicker, ClickedPagedListAdapter.ClickedHolder>(diffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClickedHolder {
        val view = parent.inflate(R.layout.item_clicked)
        return ClickedHolder(view, onClickedListener)
    }

    override fun onBindViewHolder(holder: ClickedHolder, position: Int) {
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

    class ClickedHolder(
        itemView: View,
        private val onClickedListener: OnClickedListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(clicker: Clicker) {
            itemView.tag = clicker.id
            itemView.tvClicked.text = clicker.id.toString()
            itemView.ivClicked.setImageResource(R.drawable.person1)
        }

        override fun onClick(v: View?) {
            onClickedListener.onClickedClick(v?.tag as String)
        }
    }
}