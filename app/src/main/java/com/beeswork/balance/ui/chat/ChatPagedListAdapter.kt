package com.beeswork.balance.ui.chat

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.beeswork.balance.R
import com.beeswork.balance.data.database.entity.Message
import com.beeswork.balance.internal.inflate
import kotlinx.android.synthetic.main.item_message_received.view.*
import kotlinx.android.synthetic.main.item_message_sent.view.*


class ChatPagedListAdapter: PagedListAdapter<Message, ChatPagedListAdapter.MessageViewHolder>(
    diffCallback
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return when (viewType) {
            MessageType.RECEIVED.ordinal -> MessageViewHolder(parent.inflate(R.layout.item_message_received))
            MessageType.SENT.ordinal -> MessageViewHolder(parent.inflate(R.layout.item_message_sent))
            else -> MessageViewHolder(parent.inflate(R.layout.item_message_received))
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        getItem(position)?.let {
            when (holder.itemViewType) {
                MessageType.RECEIVED.ordinal -> holder.bindMessageReceived(it)
                MessageType.SENT.ordinal -> holder.bindMessageSent(it)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.let {
            return if (it.received) MessageType.RECEIVED.ordinal else MessageType.SENT.ordinal
        } ?: kotlin.run {
            return MessageType.RECEIVED.ordinal
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean =
                oldItem == newItem
        }
    }

    enum class MessageType {
        RECEIVED,
        SENT
    }

    class MessageViewHolder(
        itemView: View
    ): RecyclerView.ViewHolder(itemView) {
        fun bindMessageSent(message: Message) {
            itemView.tvMessageSentMessage.text = "messageId: ${message.messageId} | ${message.message}"

            when (message.status) {
                Message.Status.SENT -> {
                    itemView.tvMessageSentCreatedAt.text = message.createdAt.toString()
                    showLayout(itemView, View.VISIBLE, View.GONE, View.GONE)
                }
                Message.Status.SENDING -> showLayout(itemView, View.GONE, View.VISIBLE, View.GONE)
                Message.Status.ERROR -> showLayout(itemView, View.GONE, View.GONE, View.VISIBLE)
            }
        }

        fun bindMessageReceived(message: Message) {
            itemView.tvMessageReceivedMessage.text = message.message
            itemView.tvMessageReceivedCreatedAt.text = message.createdAt.toString()
        }

        companion object {
            fun showLayout(itemView: View, createdAt: Int, loading: Int, errorOptions: Int) {
                itemView.tvMessageSentCreatedAt.visibility = createdAt
                itemView.skvMessageSentLoading.visibility = loading
                itemView.llMessageSentErrorOptions.visibility = errorOptions
            }
        }
    }





}