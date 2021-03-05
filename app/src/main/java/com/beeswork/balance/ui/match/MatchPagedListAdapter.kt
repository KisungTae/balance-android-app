package com.beeswork.balance.ui.match

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.internal.util.inflate
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_match.view.*

class MatchPagedListAdapter(
    private val onMatchListener: OnMatchListener,
    private val context: Context
) : PagedListAdapter<MatchDomain, MatchPagedListAdapter.MatchHolder>(diffCallback) {

    private val colorTextGrey: Int = ContextCompat.getColor(context, R.color.textGrey)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchHolder {
        return MatchHolder(parent.inflate(R.layout.item_match), onMatchListener)
    }

    override fun onBindViewHolder(holder: MatchHolder, position: Int) {
        getItem(position)?.let {
            val itemView = holder.itemView
            itemView.tvMatchName.text = it.name
            itemView.tvMatchUnreadIndicator.visibility = if (it.unread) View.VISIBLE else View.GONE
            itemView.tvMatchRecentChatMessage.text = getRecentChatMessage(it)
            itemView.tvMatchUpdatedAt.text = it.updatedAt.toLocalDate().toString()

            if (it.unmatched || it.deleted) {
                itemView.ivMatchProfilePicture.setImageResource(R.drawable.ic_baseline_account_circle)
                itemView.tvMatchName.setTextColor(colorTextGrey)
                itemView.tvMatchRecentChatMessage.setTextColor(colorTextGrey)
                itemView.tvMatchUpdatedAt.setTextColor(colorTextGrey)
            } else if (!it.active) {
                Glide.with(context).load(it.repPhotoKey)
            } else {

            }

        }
    }





    private fun getRecentChatMessage(matchDomain: MatchDomain): String {
        return if (matchDomain.deleted) context.getString(R.string.match_deleted_recent_chat_message)
        else if (matchDomain.unmatched) context.getString(R.string.match_unmatched_recent_chat_message)
        else if (!matchDomain.active) context.getString(R.string.match_new_recent_chat_message)
        else matchDomain.recentChatMessage
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<MatchDomain>() {
            override fun areItemsTheSame(oldItem: MatchDomain, newItem: MatchDomain): Boolean =
                oldItem.chatId == newItem.chatId

            override fun areContentsTheSame(oldItem: MatchDomain, newItem: MatchDomain): Boolean =
                oldItem == newItem
        }
    }

    class MatchHolder(
        itemView: View,
        private val onMatchListener: OnMatchListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            onMatchListener.onMatchClick(view, layoutPosition)
        }
    }

    interface OnMatchListener {
        fun onMatchClick(view: View, position: Int)
    }


}


// TODO
//  1. if lastChatMessageId <= 0 then circle the profile picture when not deleted, blocked, unmatched
