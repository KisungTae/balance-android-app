package com.beeswork.balance.ui.clicked

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.database.entity.Clicked
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.internal.inflate
import com.beeswork.balance.ui.match.MatchPagedListAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_match.view.*

class ClickedPagedListAdapter(
    private val onSwipeListener: OnSwipeListener
): PagedListAdapter<Clicked, ClickedPagedListAdapter.ClickedHolder>(ClickedPagedListAdapter.diffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClickedPagedListAdapter.ClickedHolder {
        val view = parent.inflate(R.layout.item_match)
        return ClickedPagedListAdapter.ClickedHolder(view, onSwipeListener)
    }

    override fun onBindViewHolder(holder: ClickedPagedListAdapter.ClickedHolder, position: Int) {
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

    interface OnSwipeListener {
        fun onSwipeRight()
        fun onSwipeLeft()
    }

    class ClickedHolder(
        itemView: View,
        private val onSwipeListener: ClickedPagedListAdapter.OnSwipeListener
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(clicked: Clicked) {

        }
    }
}