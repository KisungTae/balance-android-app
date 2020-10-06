package com.beeswork.balance.ui.chat

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.entity.Match
import com.beeswork.balance.data.entity.Message
import com.beeswork.balance.internal.inflate
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_match.view.*
import kotlinx.android.synthetic.main.item_message.view.*

class ChatRecyclerViewAdapter: PagedListAdapter<Message, ChatRecyclerViewAdapter.MessageViewHolder>(diffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(parent.inflate(R.layout.item_message))
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        println("binding message at $position | message = ${getItem(position)}" )
        holder.bind(getItem(position))
    }

    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean =
                oldItem == newItem
        }
    }

    class MessageViewHolder(
        itemView: View
    ): RecyclerView.ViewHolder(itemView) {

        fun bind(message: Message?) {
            itemView.tvMessage.text = message?.message ?: ""

//            itemView.tvMessageCreatedAt.text = message?.createdAt?.toString() ?: ""
            itemView.tvMessageCreatedAt.text = message?.createdAt?.toLocalTime().toString()
        }

    }



}