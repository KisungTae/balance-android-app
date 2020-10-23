package com.beeswork.balance.ui.match

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.internal.inflate
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_match.view.*

class MatchPagedListAdapter(
    private val onMatchListener: OnMatchListener
): PagedListAdapter<Match, MatchPagedListAdapter.MatchHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchHolder {
        val view = parent.inflate(R.layout.item_match)
        return MatchHolder(view, onMatchListener)
    }

    override fun onBindViewHolder(holder: MatchHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<Match>() {
            override fun areItemsTheSame(oldItem: Match, newItem: Match): Boolean =
                oldItem.chatId == newItem.chatId

            override fun areContentsTheSame(oldItem: Match, newItem: Match): Boolean =
                oldItem == newItem
        }
    }

    interface OnMatchListener {
        fun onMatchClick(view: View, chatId: Long)
    }

    class MatchHolder(
        itemView: View,
        private val onMatchListener: OnMatchListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(match: Match) {

            Picasso.get().load(R.drawable.personsmall).into(itemView.ivMatch)
            itemView.tvMatchName.text = match.toString()
//            itemView.tvMatchRecentMessage.text = match.recentMessage
//            itemView.tvMatchUnmatch.text = match.unmatched.toString()
            itemView.tag = match.chatId
        }

        override fun onClick(view: View) {
            onMatchListener.onMatchClick(view, view.tag.toString().toLong())
        }
    }

}