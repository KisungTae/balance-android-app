package com.beeswork.balance.ui.clicked

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.database.entity.Clicked
import com.beeswork.balance.internal.inflate
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.item_clicked.view.*


class ClickedPagedListAdapter(
    private val onClickedSwipeListener: OnClickedSwipeListener
): PagedListAdapter<Clicked, ClickedPagedListAdapter.ClickedHolder>(diffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClickedHolder {
        val view = parent.inflate(R.layout.item_clicked)
        return ClickedHolder(view, onClickedSwipeListener)
    }

    override fun onBindViewHolder(holder: ClickedHolder, position: Int) {
        holder.bind(getItem(position)!!)

    }

    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<Clicked>() {
            override fun areItemsTheSame(oldItem: Clicked, newItem: Clicked): Boolean =
                oldItem.swiperId == newItem.swiperId

            override fun areContentsTheSame(oldItem: Clicked, newItem: Clicked): Boolean =
                oldItem == newItem
        }
    }

    interface OnClickedSwipeListener {
        fun onSwipeRight()
        fun onSwipeLeft()
    }

    class ClickedHolder(
        itemView: View,
        private val onClickedSwipeListener: OnClickedSwipeListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(clicked: Clicked) {
            Picasso.get().load(R.drawable.person1).into(itemView.ivClicked)
        }

        override fun onClick(v: View?) {
            println("clicked clicked hahaha that's a joke? are you serious?")

        }
    }
}